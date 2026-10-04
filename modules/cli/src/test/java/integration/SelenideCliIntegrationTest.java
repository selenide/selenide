package integration;

import com.codeborne.selenide.cli.SelenideCli;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end check of the daemon/client model: several separate {@code SelenideCli.run(...)}
 * invocations drive one session (client → auto-spawned daemon subprocess → headless browser), and
 * the recording accumulates across invocations into generated Selenide code.
 *
 * <p>Runs under a browser task (e.g. {@code ./gradlew :modules:cli:chrome_headless}); excluded from
 * the browser-less {@code check} task like all {@code integration/**} tests.
 */
class SelenideCliIntegrationTest {
  @Test
  void recordsAcrossInvocationsAndGeneratesCode() throws IOException {
    Path page = Files.createTempFile("selenide-cli", ".html");
    Files.writeString(page, "<html><body><input id='name'><button id='go'>Go</button></body></html>", UTF_8);
    String session = "itest-" + System.nanoTime();
    String url = page.toUri().toString();

    try {
      assertThat(cli("-s", session, "open", "--headless", "--browser=chrome", url)).isZero();
      assertThat(cli("-s", session, "setValue", "#name", "Jane")).isZero();
      assertThat(cli("-s", session, "click", "#go")).isZero();
      assertThat(cli("-s", session, "should", "#name", "value", "Jane")).isZero();

      Captured code = capture("-s", session, "code");
      assertThat(code.exitCode).isZero();
      assertThat(code.out)
        .contains("$(\"#name\").setValue(\"Jane\");")
        .contains("$(\"#go\").click();")
        .contains("$(\"#name\").shouldHave(value(\"Jane\"));");
    }
    finally {
      cli("-s", session, "close");
      Files.deleteIfExists(page);
    }
  }

  private static int cli(String... args) {
    return capture(args).exitCode;
  }

  private static Captured capture(String... args) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    PrintStream outStream = new PrintStream(out, true, UTF_8);
    PrintStream errStream = new PrintStream(err, true, UTF_8);
    try {
      int code = SelenideCli.run(new ArrayList<>(List.of(args)), outStream, errStream);
      return new Captured(code, out.toString(UTF_8), err.toString(UTF_8));
    }
    finally {
      outStream.close();
      errStream.close();
    }
  }

  private record Captured(int exitCode, String out, String err) {
  }
}
