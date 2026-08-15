package com.codeborne.selenide.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.List;
import java.util.OptionalInt;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the client ↔ daemon round-trip over real loopback sockets with a fake executor
 * (no browser). Points {@code user.home} at a temp dir so session files stay isolated.
 *
 * <p>Declares a write lock on the "user.home" system property so JUnit serializes this class
 * against other test classes that read or write it, instead of racing on that JVM-wide property
 * across concurrently-run test classes (see gradle/tests.gradle).
 */
@ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
class DaemonRoundTripTest {
  @TempDir
  Path home;

  private String originalHome;

  @BeforeEach
  void redirectHome() {
    originalHome = System.getProperty("user.home");
    System.setProperty("user.home", home.toString());
  }

  @AfterEach
  void restoreHome() {
    System.setProperty("user.home", originalHome);
  }

  @Test
  void clientDrivesDaemonAndShutsItDown() throws InterruptedException {
    FakeExecutor executor = new FakeExecutor();
    Thread daemon = new Thread(() -> Daemon.run("t", executor), "daemon-under-test");
    daemon.setDaemon(true);
    daemon.start();
    awaitPort();

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    DaemonClient client = new DaemonClient("t", new PrintStream(out, true, UTF_8), new PrintStream(err, true, UTF_8));

    assertThat(client.command(List.of("setValue", "#q", "hello world"))).isZero();
    assertThat(out.toString(UTF_8)).contains("ran: setValue #q hello world");

    out.reset();
    err.reset();
    assertThat(client.command(List.of("boom"))).isEqualTo(1);
    assertThat(err.toString(UTF_8)).contains("bad");

    assertThat(client.command(List.of("close"))).isZero();
    // The browser must already be shut down by the time the client sees success - not merely
    // scheduled to shut down once the daemon thread eventually unwinds.
    assertThat(executor.shutdownCalled).isTrue();
    daemon.join(3000);
    assertThat(daemon.isAlive()).isFalse();
    assertThat(SessionStore.readPort("t")).isEmpty();

    err.reset();
    assertThat(client.command(List.of("click", "#x"))).isEqualTo(1);
    assertThat(err.toString(UTF_8)).contains("No open session");
  }

  @Test
  void shutdownIntentSurvivesClientDisconnectingBeforeReadingTheResponse() throws Exception {
    FakeExecutor executor = new FakeExecutor();
    Thread daemon = new Thread(() -> Daemon.run("t2", executor), "daemon-under-test-2");
    daemon.setDaemon(true);
    daemon.start();
    awaitPort("t2");

    OptionalInt port = SessionStore.readPort("t2");
    assertThat(port).isPresent();
    try (Socket socket = new Socket()) {
      socket.connect(new InetSocketAddress(InetAddress.getLoopbackAddress(), port.getAsInt()), 500);
      Protocol.writeRequest(socket.getOutputStream(), List.of("close"));
      // Disconnect immediately instead of reading the response, simulating a client that goes away
      // while the daemon is (or is about to start) writing its acknowledgement.
    }

    daemon.join(3000);
    assertThat(daemon.isAlive()).isFalse();
    assertThat(executor.shutdownCalled).isTrue();
  }

  @Test
  void closeAllReportsFailureWhenASessionFailsToClose() throws Exception {
    FakeExecutor healthyExecutor = new FakeExecutor();
    Thread healthyDaemon = new Thread(() -> Daemon.run("healthy", healthyExecutor), "daemon-healthy");
    healthyDaemon.setDaemon(true);
    healthyDaemon.start();
    awaitPort("healthy");

    try (ServerSocket brokenServer = new ServerSocket()) {
      brokenServer.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
      SessionStore.writePort("broken", brokenServer.getLocalPort());
      Thread brokenDaemon = new Thread(() -> respondWithFailure(brokenServer), "daemon-broken");
      brokenDaemon.setDaemon(true);
      brokenDaemon.start();

      // A port file pointing at a port nothing listens on anymore, i.e. a stale/dead session.
      int deadPort;
      try (ServerSocket temp = new ServerSocket()) {
        temp.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
        deadPort = temp.getLocalPort();
      }
      SessionStore.writePort("stale", deadPort);

      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ByteArrayOutputStream err = new ByteArrayOutputStream();
      PrintStream outStream = new PrintStream(out, true, UTF_8);
      PrintStream errStream = new PrintStream(err, true, UTF_8);
      DaemonClient client = new DaemonClient("irrelevant-for-closeAll", outStream, errStream);

      assertThat(client.closeAll()).isEqualTo(1);
      assertThat(err.toString(UTF_8)).contains("failed to close 1 session(s)");
      assertThat(SessionStore.readPort("stale")).isEmpty();

      healthyDaemon.join(3000);
      assertThat(healthyDaemon.isAlive()).isFalse();
      brokenDaemon.join(3000);
    }
  }

  /**
   * Accepts connections until it sees a real request, responding to it with a failure - and
   * ignoring earlier connections that disconnect without sending anything, since closeAll() probes
   * liveness (connect, then immediately disconnect) before it sends the actual "close" request.
   */
  private static void respondWithFailure(ServerSocket server) {
    while (!server.isClosed()) {
      try (Socket socket = server.accept()) {
        List<String> request = Protocol.readRequest(socket.getInputStream());
        if (!request.isEmpty()) {
          Protocol.writeResponse(socket.getOutputStream(), false, false, "boom");
          return;
        }
      }
      catch (IOException e) {
        // A liveness probe (or the server socket closing at test teardown); keep looping.
      }
    }
  }

  private static void awaitPort() throws InterruptedException {
    awaitPort("t");
  }

  private static void awaitPort(String session) throws InterruptedException {
    long deadline = System.currentTimeMillis() + 5000;
    while (System.currentTimeMillis() < deadline && SessionStore.readPort(session).isEmpty()) {
      Thread.sleep(50);
    }
    assertThat(SessionStore.readPort(session)).isPresent();
  }

  private static final class FakeExecutor implements CommandExecutor {
    private volatile boolean shutdownCalled;

    @Override
    public Result execute(List<String> args) {
      if (!args.isEmpty() && args.get(0).equals("close")) {
        return Result.shutdown("closed");
      }
      if (!args.isEmpty() && args.get(0).equals("boom")) {
        return Result.error("bad selector");
      }
      return Result.ok("ran: " + String.join(" ", args));
    }

    @Override
    public void shutdown() {
      shutdownCalled = true;
    }
  }
}
