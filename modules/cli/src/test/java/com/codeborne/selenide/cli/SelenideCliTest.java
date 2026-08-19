package com.codeborne.selenide.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Every one of these commands has no daemon running for its session, so any behaviour beyond
 * "print usage" or "unknown command" would mean it fell through to {@link DaemonClient}.
 *
 * <p>Declares a write lock on the "user.home" system property so JUnit serializes this class
 * against other test classes that read or write it, instead of racing on that JVM-wide property
 * across concurrently-run test classes (see gradle/tests.gradle).
 */
@ResourceLock(value = Resources.SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
class SelenideCliTest {
  @TempDir
  Path home;

  private String originalHome;
  private final ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
  private final PrintStream out = new PrintStream(outBuffer, true, UTF_8);
  private final PrintStream err = new PrintStream(errBuffer, true, UTF_8);

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
  void printsUsageWhenCalledWithNoArgs() {
    int exitCode = SelenideCli.run(List.of(), out, err);

    assertThat(exitCode).isEqualTo(0);
    assertThat(outBuffer.toString(UTF_8)).contains("Selenide CLI", "Lifecycle:", "Actions (recorded):");
  }

  @Test
  void helpFlagOnAKnownCommandPrintsUsageInsteadOfContactingTheDaemon() {
    int exitCode = SelenideCli.run(List.of("should", "--help"), out, err);

    assertThat(exitCode).isEqualTo(0);
    assertThat(outBuffer.toString(UTF_8)).contains("Selenide CLI");
    assertThat(errBuffer.toString(UTF_8)).doesNotContain("No open session");
  }

  @Test
  void shortHelpFlagOnOpenPrintsUsageInsteadOfARequiredUrlError() {
    int exitCode = SelenideCli.run(List.of("open", "-h"), out, err);

    assertThat(exitCode).isEqualTo(0);
    assertThat(outBuffer.toString(UTF_8)).contains("Selenide CLI");
    assertThat(errBuffer.toString(UTF_8)).isEmpty();
  }

  @Test
  void unknownCommandIsRejectedWithoutContactingTheDaemon() {
    int exitCode = SelenideCli.run(List.of("bogus", "#selector"), out, err);

    assertThat(exitCode).isEqualTo(1);
    assertThat(errBuffer.toString(UTF_8)).contains("Unknown command: 'bogus'").contains("selenide --help");
  }

  @Test
  void knownCommandWithNoRunningDaemonStillReportsNoOpenSession() {
    int exitCode = SelenideCli.run(List.of("should", "#msg", "visible"), out, err);

    assertThat(exitCode).isEqualTo(1);
    assertThat(errBuffer.toString(UTF_8)).contains("No open session 'default'");
  }

  @Test
  void installCommandIsDispatchedToSkillInstallerNotTheDaemon() {
    // No --skills flag, so this only exercises argument validation - no filesystem writes into
    // this test's real working directory (see SkillInstallerTest for the actual install/copy logic).
    int exitCode = SelenideCli.run(List.of("install"), out, err);

    assertThat(exitCode).isEqualTo(1);
    assertThat(errBuffer.toString(UTF_8)).contains("Usage: selenide install --skills[=agents]");
  }

  @Test
  void commandNameMatchingIsCaseInsensitive() {
    int exitCode = SelenideCli.run(List.of("SetValue", "#email", "a@b.com"), out, err);

    assertThat(exitCode).isEqualTo(1);
    assertThat(errBuffer.toString(UTF_8)).contains("No open session 'default'");
  }

  @Test
  void mixedCaseOpenReachesTheOpenHandlerInsteadOfTheDaemonCommandPath() {
    // "open" is also a CommandInterpreter action name, so a case mismatch used to slip past the
    // known-command check but miss the case-sensitive switch, silently landing in the generic
    // DaemonClient.command() path instead of DaemonClient.open() (which alone knows how to spawn
    // a daemon). No URL given, so this only exercises dispatch, not a real daemon spawn.
    int exitCode = SelenideCli.run(List.of("OPEN"), out, err);

    assertThat(exitCode).isEqualTo(1);
    assertThat(errBuffer.toString(UTF_8))
      .contains("Usage: selenide open <url> [options]")
      .doesNotContain("No open session");
  }

  @Test
  void mixedCaseListIsDispatchedToTheListHandler() {
    int exitCode = SelenideCli.run(List.of("LIST"), out, err);

    assertThat(exitCode).isEqualTo(0);
    assertThat(outBuffer.toString(UTF_8)).contains("No sessions.");
    assertThat(errBuffer.toString(UTF_8)).doesNotContain("Unknown command");
  }

  @Test
  void mixedCaseCloseAllIsDispatchedToTheCloseAllHandler() {
    int exitCode = SelenideCli.run(List.of("CLOSE-ALL"), out, err);

    assertThat(exitCode).isEqualTo(0);
    assertThat(outBuffer.toString(UTF_8)).contains("closed all sessions");
    assertThat(errBuffer.toString(UTF_8)).doesNotContain("Unknown command");
  }

  @Test
  void mixedCaseVersionIsDispatchedToTheVersionHandler() {
    int exitCode = SelenideCli.run(List.of("VERSION"), out, err);

    assertThat(exitCode).isEqualTo(0);
    assertThat(outBuffer.toString(UTF_8)).contains("selenide-cli");
    assertThat(errBuffer.toString(UTF_8)).doesNotContain("Unknown command");
  }

  @Test
  void mixedCaseInstallIsDispatchedToSkillInstaller() {
    int exitCode = SelenideCli.run(List.of("INSTALL"), out, err);

    assertThat(exitCode).isEqualTo(1);
    assertThat(errBuffer.toString(UTF_8)).contains("Usage: selenide install --skills[=agents]");
  }

  @Test
  void mixedCaseInstallDoesNotNagAboutTheStaleSkillItIsAboutToReplace() throws Exception {
    // The stale-skill guard used to compare the un-normalized command ("INSTALL" != "install"), so
    // it fired even for the command about to fix that exact staleness. Planting the stale copy
    // under the redirected home (--global target) - never the real ~/.claude - to reproduce it.
    Path skillMd = home.resolve(".claude/skills/selenide-cli/SKILL.md");
    Files.createDirectories(skillMd.getParent());
    Files.writeString(skillMd, "stale content, deliberately does not match the bundled SKILL.md");

    int exitCode = SelenideCli.run(List.of("INSTALL"), out, err);

    assertThat(exitCode).isEqualTo(1); // still errors: no --skills flag given
    assertThat(errBuffer.toString(UTF_8))
      .contains("Usage: selenide install --skills[=agents]")
      .doesNotContain("does not match the tool version");
  }
}
