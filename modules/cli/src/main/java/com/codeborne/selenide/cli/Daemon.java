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
    try (ServerSocket server = new ServerSocket()) {
      server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
      SessionStore.writePort(session, server.getLocalPort());
      try {
        acceptLoop(server, executor);
      }
      finally {
        SessionStore.delete(session);
        executor.shutdown();
      }
    }
    catch (IOException e) {
      throw new UncheckedIOException(e);
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
    // accept() failures propagate and stop the loop; per-connection IO errors are swallowed below.
    Socket accepted = server.accept();
    try (Socket socket = accepted) {
      List<String> args = Protocol.readRequest(socket.getInputStream());
      CommandExecutor.Result result = safeExecute(executor, args);
      Protocol.writeResponse(socket.getOutputStream(), result.ok(), result.shutdown(), result.output());
      return result.shutdown();
    }
    catch (IOException e) {
      // A client disconnected mid-command; keep serving other clients.
      return false;
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
