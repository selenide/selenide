package com.codeborne.selenide.cli;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Background server that owns one browser session. It binds a loopback socket, publishes its port
 * via {@link SessionStore}, and serves one command per client connection until a command asks it to
 * shut down. Runs single-threaded so all browser work (and the thread-local SelenideLogger recorder)
 * stays on one thread.
 */
final class Daemon {
  private Daemon() {
  }

  static void run(String session, CommandExecutor executor) {
    // isAlive()/sendTo() only prove a TCP connect to the recorded port succeeds, not that the
    // listener is still this session's daemon - if the process dies without deleting its port file
    // (e.g. SIGTERM, Ctrl-C), a later process could in theory bind that same freed ephemeral port.
    // A shutdown hook closes that window for every termination path except SIGKILL, which no process
    // can ever intercept in any language; narrowing that residual gap further would need a
    // handshake in the wire protocol itself, which isn't worth the added complexity for a same-user,
    // localhost-only, single-daemon CLI tool.
    Thread cleanupHook = new Thread(() -> SessionStore.delete(session), "selenide-cli-cleanup");
    Runtime.getRuntime().addShutdownHook(cleanupHook);
    try (ServerSocket server = new ServerSocket()) {
      server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
      SessionStore.writePort(session, server.getLocalPort());
      try {
        acceptLoop(server, executor);
      }
      finally {
        SessionStore.delete(session);
        // Safety net for abnormal exits (e.g. accept() failing) where serveOne() never got a chance
        // to shut down the executor itself; harmless to call again after a graceful "close".
        executor.shutdown();
      }
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    finally {
      removeShutdownHookIfNotAlreadyShuttingDown(cleanupHook);
    }
  }

  private static void removeShutdownHookIfNotAlreadyShuttingDown(Thread cleanupHook) {
    try {
      Runtime.getRuntime().removeShutdownHook(cleanupHook);
    }
    catch (IllegalStateException e) {
      // The JVM is already shutting down (the hook itself is what's about to run, or already ran);
      // nothing to undo.
    }
  }

  private static void acceptLoop(ServerSocket server, CommandExecutor executor) throws IOException {
    while (!serveOne(server, executor)) {
      // keep serving until a command requests shutdown
    }
  }

  /**
   * @return true when the served command asked the daemon to stop.
   */
  private static boolean serveOne(ServerSocket server, CommandExecutor executor) throws IOException {
    // accept()/readRequest() failures propagate and stop the loop; a disconnect while reading a
    // request means no command ran, so it's safe to just keep serving other clients.
    Socket accepted = server.accept();
    try (Socket socket = accepted) {
      List<String> args = Protocol.readRequest(socket.getInputStream());
      CommandExecutor.Result result = safeExecute(executor, args);
      if (result.shutdown()) {
        // Shut down before acknowledging it, so a caller can never observe "closed" success before
        // the browser has actually finished tearing down.
        executor.shutdown();
      }
      writeResponseQuietly(socket, result);
      return result.shutdown();
    }
    catch (IOException e) {
      return false;
    }
  }

  private static void writeResponseQuietly(Socket socket, CommandExecutor.Result result) {
    try {
      Protocol.writeResponse(socket.getOutputStream(), result.ok(), result.shutdown(), result.output());
    }
    catch (IOException e) {
      // The client disconnected while we were writing the response. The command already ran, so
      // its shutdown intent (if any) - already applied above - must not be lost just because the
      // client never received the acknowledgement.
    }
  }

  private static CommandExecutor.Result safeExecute(CommandExecutor executor, List<String> args) {
    try {
      return executor.execute(args);
    }
    catch (RuntimeException e) {
      return CommandExecutor.Result.error("internal error: " + e);
    }
  }
}
