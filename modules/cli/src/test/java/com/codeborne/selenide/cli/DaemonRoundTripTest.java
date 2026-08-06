package com.codeborne.selenide.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the client ↔ daemon round-trip over real loopback sockets with a fake executor
 * (no browser). Points {@code user.home} at a temp dir so session files stay isolated.
 */
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
    daemon.join(3000);
    assertThat(daemon.isAlive()).isFalse();
    assertThat(executor.shutdownCalled).isTrue();
    assertThat(SessionStore.readPort("t")).isEmpty();

    err.reset();
    assertThat(client.command(List.of("click", "#x"))).isEqualTo(1);
    assertThat(err.toString(UTF_8)).contains("No open session");
  }

  private static void awaitPort() throws InterruptedException {
    long deadline = System.currentTimeMillis() + 5000;
    while (System.currentTimeMillis() < deadline && SessionStore.readPort("t").isEmpty()) {
      Thread.sleep(50);
    }
    assertThat(SessionStore.readPort("t")).isPresent();
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
