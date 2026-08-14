package integration;

import com.codeborne.selenide.cli.SelenideCli;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression test for a residual risk in DaemonClient.isAlive(): it only proves a TCP connect to
 * the recorded port succeeds, not that the listener is still this session's daemon. If a daemon
 * died without deleting its port file, a later process could in theory bind that same freed
 * ephemeral port. Daemon.run() registers a JVM shutdown hook precisely to close that window for
 * every termination path except SIGKILL (which no process can ever intercept) - this verifies the
 * hook actually deletes the port file when the daemon is sent SIGTERM, e.g. `kill <pid>` (as
 * opposed to `selenide close`, which the other integration test already covers).
 */
class SelenideCliSigtermCleanupTest {
  @Test
  void sigtermCleansUpTheSessionPortFile() throws Exception {
    Path page = Files.createTempFile("selenide-cli-sigterm", ".html");
    Files.writeString(page, "<html><body>bye</body></html>", UTF_8);
    String session = "sigterm-" + System.nanoTime();
    String url = page.toUri().toString();
    Path portFile = Path.of(System.getProperty("user.home"), ".selenide-cli", session + ".port");

    try {
      assertThat(cli("-s", session, "open", "--headless", "--browser=chrome", url)).isZero();
      assertThat(portFile).exists();

      ProcessHandle daemonProcess = ProcessHandle.allProcesses()
        .filter(p -> p.info().arguments().map(args -> Arrays.asList(args).contains("--session=" + session)).orElse(false))
        .findFirst()
        .orElseThrow(() -> new AssertionError("daemon process for session '" + session + "' not found"));

      daemonProcess.destroy(); // SIGTERM - NOT `selenide close`
      daemonProcess.onExit().get(10, TimeUnit.SECONDS);

      long deadline = System.currentTimeMillis() + 5000;
      while (System.currentTimeMillis() < deadline && Files.exists(portFile)) {
        Thread.sleep(50);
      }
      assertThat(portFile).doesNotExist();
    }
    finally {
      Files.deleteIfExists(page);
      Files.deleteIfExists(portFile);
    }
  }

  private static int cli(String... args) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    return SelenideCli.run(new ArrayList<>(List.of(args)), new PrintStream(out, true, UTF_8), new PrintStream(err, true, UTF_8));
  }
}
