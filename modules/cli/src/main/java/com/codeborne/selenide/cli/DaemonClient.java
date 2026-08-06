package com.codeborne.selenide.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

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
      SessionStore.delete(session);
      spawnDaemon(flagsOf(args));
      if (!waitUntilReady()) {
        err.println("Timed out starting the browser daemon. See " + SessionStore.logFile(session));
        return 1;
      }
    }
    return sendTo(session, List.of("open", url));
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
    for (String name : SessionStore.sessions()) {
      if (isAlive(name)) {
        sendTo(name, List.of("close"));
      }
      else {
        SessionStore.delete(name);
      }
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

  private boolean waitUntilReady() {
    long deadline = System.currentTimeMillis() + SPAWN_TIMEOUT_MS;
    while (System.currentTimeMillis() < deadline) {
      if (isAlive(session)) {
        return true;
      }
      sleep(POLL_INTERVAL_MS);
    }
    return isAlive(session);
  }

  private void spawnDaemon(List<String> configFlags) {
    List<String> command = new ArrayList<>();
    command.add(javaBinary());
    command.add("-cp");
    command.add(System.getProperty("java.class.path"));
    command.add(SelenideCli.class.getName());
    command.add("__daemon");
    command.add("--session=" + session);
    command.addAll(configFlags);
    try {
      Files.createDirectories(SessionStore.dir());
      ProcessBuilder builder = new ProcessBuilder(command);
      builder.redirectErrorStream(true);
      builder.redirectOutput(SessionStore.logFile(session).toFile());
      builder.start();
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static String javaBinary() {
    return Path.of(System.getProperty("java.home"), "bin", "java").toString();
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
