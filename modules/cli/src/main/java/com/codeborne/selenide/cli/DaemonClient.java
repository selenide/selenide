package com.codeborne.selenide.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * The thin, per-invocation CLI client. Resolves the session's daemon (auto-spawning one for
 * {@code open}), sends a single command over the loopback socket, prints the response, and exits.
 */
final class DaemonClient {
  private static final int CONNECT_TIMEOUT_MS = 500;
  private static final long SPAWN_TIMEOUT_MS = 20_000;
  private static final long POLL_INTERVAL_MS = 150;

  private final String session;
  private final PrintStream out;
  private final PrintStream err;

  DaemonClient(String session, PrintStream out, PrintStream err) {
    this.session = session;
    this.out = out;
    this.err = err;
  }

  /** {@code open [flags] <url>} — ensure a daemon (spawning if needed), then navigate. */
  int open(List<String> args) {
    String url = firstPositional(args);
    if (url == null) {
      err.println("Usage: selenide open <url> [options]");
      return 1;
    }
    if (!isAlive(session)) {
      String failure = spawnDaemonIfNotAlreadyRunning(flagsOf(args));
      if (failure != null) {
        err.println(failure);
        return 1;
      }
    }
    return sendTo(session, List.of("open", url));
  }

  /**
   * Ensures a daemon is running for this session, guarding the check-then-spawn sequence with a
   * per-session file lock (held across separate OS processes, unlike an in-JVM lock) so two
   * concurrent {@code open} invocations for the same session - e.g. a retried CI step - can't each
   * spawn their own daemon and orphan one of the resulting browsers.
   *
   * @return an error message if the daemon could not be started, or null on success.
   */
  private String spawnDaemonIfNotAlreadyRunning(List<String> configFlags) {
    try {
      Files.createDirectories(SessionStore.dir());
      try (FileChannel lockChannel = FileChannel.open(SessionStore.lockFile(session), CREATE, WRITE)) {
        FileLock lock = acquireLock(lockChannel);
        try {
          if (isAlive(session)) {
            // A concurrent `open` already spawned it while we were waiting for the lock.
            return null;
          }
          SessionStore.delete(session);
          Process daemonProcess = spawnDaemon(configFlags);
          return waitUntilReady(daemonProcess) ? null : daemonFailureMessage(daemonProcess);
        }
        finally {
          lock.release();
        }
      }
    }
    catch (IOException e) {
      return "Could not spawn the browser daemon: " + e.getMessage();
    }
  }

  private static FileLock acquireLock(FileChannel channel) throws IOException {
    while (true) {
      try {
        FileLock lock = channel.tryLock();
        if (lock != null) {
          return lock;
        }
      }
      catch (OverlappingFileLockException e) {
        // Another thread in this same JVM holds it. Real usage never hits this branch (each
        // `selenide open` invocation is a separate process), but keeps this safe for in-process
        // callers too (e.g. tests).
      }
      sleep(POLL_INTERVAL_MS);
    }
  }

  private String daemonFailureMessage(Process daemonProcess) {
    String logFile = "See " + SessionStore.logFile(session);
    return daemonProcess.isAlive()
      ? "Timed out starting the browser daemon. " + logFile
      : "The browser daemon exited immediately (exit code " + daemonProcess.exitValue() + "). " + logFile;
  }

  /** Any other command against an already-running session. */
  int command(List<String> args) {
    if (!isAlive(session)) {
      err.println("No open session '" + session + "'. Run: selenide open <url>");
      return 1;
    }
    return sendTo(session, args);
  }

  void list() {
    List<String> sessions = SessionStore.sessions();
    if (sessions.isEmpty()) {
      out.println("No sessions.");
      return;
    }
    for (String name : sessions) {
      out.println(name + "  " + (isAlive(name) ? "running" : "stale"));
    }
  }

  int closeAll() {
    int failures = 0;
    for (String name : SessionStore.sessions()) {
      if (isAlive(name)) {
        if (sendTo(name, List.of("close")) != 0) {
          failures++;
        }
      }
      else {
        SessionStore.delete(name);
      }
    }
    if (failures > 0) {
      err.println("failed to close " + failures + " session(s); see above for details");
      return 1;
    }
    out.println("closed all sessions");
    return 0;
  }

  private int sendTo(String targetSession, List<String> args) {
    OptionalInt port = SessionStore.readPort(targetSession);
    if (port.isEmpty()) {
      err.println("No open session '" + targetSession + "'.");
      return 1;
    }
    try (Socket socket = connect(port.getAsInt())) {
      Protocol.writeRequest(socket.getOutputStream(), args);
      Protocol.Response response = Protocol.readResponse(socket.getInputStream());
      (response.ok() ? out : err).println(response.text());
      return response.ok() ? 0 : 1;
    }
    catch (IOException e) {
      err.println("Could not reach session '" + targetSession + "': " + e.getMessage());
      return 1;
    }
  }

  private boolean isAlive(String targetSession) {
    OptionalInt port = SessionStore.readPort(targetSession);
    if (port.isEmpty()) {
      return false;
    }
    try (Socket socket = connect(port.getAsInt())) {
      return socket.isConnected();
    }
    catch (IOException e) {
      return false;
    }
  }

  private static Socket connect(int port) throws IOException {
    Socket socket = new Socket();
    socket.connect(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), CONNECT_TIMEOUT_MS);
    return socket;
  }

  private boolean waitUntilReady(Process daemonProcess) {
    long deadline = System.currentTimeMillis() + SPAWN_TIMEOUT_MS;
    while (System.currentTimeMillis() < deadline) {
      if (isAlive(session)) {
        return true;
      }
      if (!daemonProcess.isAlive()) {
        // The daemon subprocess crashed on startup (e.g. a bad config flag) - no point polling
        // for the rest of the timeout.
        return false;
      }
      sleep(POLL_INTERVAL_MS);
    }
    return isAlive(session);
  }

  private Process spawnDaemon(List<String> configFlags) {
    List<String> command = new ArrayList<>();
    command.add(javaBinary());
    command.add("-cp");
    command.add(System.getProperty("java.class.path"));
    command.add(SelenideCli.class.getName());
    command.add("__daemon");
    command.add("--session=" + session);
    command.addAll(configFlags);
    try {
      ProcessBuilder builder = new ProcessBuilder(command);
      builder.redirectErrorStream(true);
      builder.redirectOutput(SessionStore.logFile(session).toFile());
      return builder.start();
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static String javaBinary() {
    return javaBinary(System.getProperty("java.home"), System.getProperty("os.name", ""));
  }

  // ProcessBuilder/CreateProcess doesn't apply PATHEXT resolution to an explicit path on Windows,
  // so the extension-less name must be spelled out there. Takes javaHome/osName as parameters
  // (instead of reading system properties directly) so both branches are unit-testable regardless
  // of the OS actually running the tests.
  static String javaBinary(String javaHome, String osName) {
    String executable = osName.toLowerCase().contains("win") ? "java.exe" : "java";
    return Path.of(javaHome, "bin", executable).toString();
  }

  private static String firstPositional(List<String> args) {
    for (String arg : args) {
      if (!arg.startsWith("-")) {
        return arg;
      }
    }
    return null;
  }

  private static List<String> flagsOf(List<String> args) {
    List<String> flags = new ArrayList<>();
    for (String arg : args) {
      if (arg.startsWith("--")) {
        flags.add(arg);
      }
    }
    return flags;
  }

  private static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
