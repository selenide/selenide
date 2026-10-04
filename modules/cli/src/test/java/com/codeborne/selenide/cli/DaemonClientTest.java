package com.codeborne.selenide.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.io.TempDirDeletionStrategy.IgnoreFailures;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises DaemonClient.open() against a real spawned {@code __daemon} subprocess (no browser
 * involved - an invalid config flag crashes the subprocess before it ever touches Selenium), to
 * verify that crash is reported quickly instead of only after the full spawn timeout.
 *
 * <p>Declares a write lock on the "user.home" system property so JUnit serializes this class
 * against other test classes that read or write it, instead of racing on that JVM-wide property
 * across concurrently-run test classes (see gradle/tests.gradle).
 */
@ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
class DaemonClientTest {
  // On Windows, the log file of the crashed daemon may still be locked when JUnit deletes the temp dir
  @TempDir(deletionStrategy = IgnoreFailures.class)
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
  void reportsABadConfigFlagQuicklyInsteadOfWaitingTheFullSpawnTimeout() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    DaemonClient client = new DaemonClient("bad-flag-session", new PrintStream(out, true, UTF_8), new PrintStream(err, true, UTF_8));

    long start = System.currentTimeMillis();
    int exitCode = client.open(List.of("--timeout=5ooo", "https://example.com"));
    long elapsed = System.currentTimeMillis() - start;

    assertThat(exitCode).isEqualTo(1);
    assertThat(err.toString(UTF_8)).contains("exited immediately");
    // The full spawn timeout is 20s; a bad flag should be detected in well under that.
    assertThat(elapsed).isLessThan(15_000L);
  }

  @Test
  void javaBinaryAppendsExeOnlyOnWindows() {
    String javaHome = "some-jdk-home";
    assertThat(DaemonClient.javaBinary(javaHome, "Linux")).isEqualTo(Path.of(javaHome, "bin", "java").toString());
    assertThat(DaemonClient.javaBinary(javaHome, "Mac OS X")).isEqualTo(Path.of(javaHome, "bin", "java").toString());
    assertThat(DaemonClient.javaBinary(javaHome, "Windows 11")).isEqualTo(Path.of(javaHome, "bin", "java.exe").toString());
  }

  @Test
  void escapesQuotesOnlyOnWindows() {
    List<String> args = List.of("--capability=custom:json={\"a\":\"b\\\"c\"}", "--reports-folder=C:\\reports\\");
    assertThat(DaemonClient.escapeQuotes(args, "Linux", "true")).isSameAs(args);
    assertThat(DaemonClient.escapeQuotes(args, "Windows 11", "false")).isSameAs(args);
    assertThat(DaemonClient.escapeQuotes(args, "Windows 11", "true")).containsExactly(
      "--capability=custom:json={\\\"a\\\":\\\"b\\\\\\\"c\\\"}",
      "--reports-folder=C:\\reports\\"
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"true", "false"})
  void passesConfigFlagsWithQuotesToSubprocessIntact(String allowAmbiguousCommands) throws IOException, InterruptedException {
    List<String> flags = List.of(
      "--capability=goog:chromeOptions={\"args\":[\"--no-sandbox\",\"--lang=en\"]}",
      "--capability=custom:json={\"key\":\"value with spaces\"}",
      "--reports-folder=C:\\temp\\reports",
      "--browser=chrome"
    );
    List<String> command = new ArrayList<>(List.of(
      DaemonClient.javaBinary(System.getProperty("java.home"), System.getProperty("os.name")),
      "-cp", System.getProperty("java.class.path"), EchoArgs.class.getName()
    ));
    command.addAll(DaemonClient.escapeQuotes(flags, System.getProperty("os.name"), allowAmbiguousCommands));

    Process process = startWithAllowAmbiguousCommands(command, allowAmbiguousCommands);
    String output = new String(process.getInputStream().readAllBytes(), UTF_8);

    assertThat(process.waitFor()).isZero();
    assertThat(output.lines().toList()).isEqualTo(flags);
  }

  private static Process startWithAllowAmbiguousCommands(List<String> command, String allowAmbiguousCommands) throws IOException {
    String original = System.getProperty("jdk.lang.Process.allowAmbiguousCommands");
    System.setProperty("jdk.lang.Process.allowAmbiguousCommands", allowAmbiguousCommands);
    try {
      return new ProcessBuilder(command).redirectErrorStream(true).start();
    }
    finally {
      if (original == null) {
        System.clearProperty("jdk.lang.Process.allowAmbiguousCommands");
      }
      else {
        System.setProperty("jdk.lang.Process.allowAmbiguousCommands", original);
      }
    }
  }
}
