package com.codeborne.selenide.cli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises install/warnIfStale through the testable overloads that take explicit cwd/home paths,
 * so nothing here ever touches the real filesystem outside a {@link TempDir} (unlike the real
 * {@code user.home}-based entry points, which are exercised only manually / in the CLI itself).
 */
class SkillInstallerTest {
  @TempDir
  Path cwd;
  @TempDir
  Path home;

  private final ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errBuffer = new ByteArrayOutputStream();
  private final PrintStream out = new PrintStream(outBuffer, true, UTF_8);
  private final PrintStream err = new PrintStream(errBuffer, true, UTF_8);

  @Test
  void requiresTheSkillsFlag() {
    int exitCode = SkillInstaller.install(List.of(), out, err, cwd, home);

    assertThat(exitCode).isEqualTo(1);
    assertThat(errBuffer.toString(UTF_8)).contains("Usage: selenide install --skills[=agents]");
  }

  @Test
  void rejectsAnUnknownSkillsValue() {
    int exitCode = SkillInstaller.install(List.of("--skills=cursor"), out, err, cwd, home);

    assertThat(exitCode).isEqualTo(1);
    assertThat(errBuffer.toString(UTF_8)).contains("Unknown --skills value: 'cursor'");
  }

  @Test
  void installsIntoDotClaudeSkillsByDefault() throws Exception {
    int exitCode = SkillInstaller.install(List.of("--skills"), out, err, cwd, home);

    assertThat(exitCode).isEqualTo(0);
    Path skillMd = cwd.resolve(".claude/skills/selenide-cli/SKILL.md");
    assertThat(skillMd).exists();
    assertThat(Files.readString(skillMd, UTF_8)).contains("name: selenide-cli");
    assertThat(cwd.resolve(".claude/skills/selenide-cli/references/commands.md")).exists();
    assertThat(outBuffer.toString(UTF_8)).contains("Installed selenide-cli skill to");
  }

  @Test
  void installsIntoDotAgentsSkillsWhenRequested() {
    int exitCode = SkillInstaller.install(List.of("--skills=agents"), out, err, cwd, home);

    assertThat(exitCode).isEqualTo(0);
    assertThat(cwd.resolve(".agents/skills/selenide-cli/SKILL.md")).exists();
    assertThat(cwd.resolve(".claude/skills/selenide-cli/SKILL.md")).doesNotExist();
  }

  @Test
  void globalFlagTargetsHomeInsteadOfCwd() {
    int exitCode = SkillInstaller.install(List.of("--skills", "--global"), out, err, cwd, home);

    assertThat(exitCode).isEqualTo(0);
    assertThat(home.resolve(".claude/skills/selenide-cli/SKILL.md")).exists();
    assertThat(cwd.resolve(".claude/skills/selenide-cli")).doesNotExist();
  }

  @Test
  void doesNotWarnWhenNoSkillIsInstalled() {
    SkillInstaller.warnIfStale(err, cwd);

    assertThat(errBuffer.toString(UTF_8)).isEmpty();
  }

  @Test
  void doesNotWarnRightAfterInstalling() {
    SkillInstaller.install(List.of("--skills"), out, err, cwd, home);

    SkillInstaller.warnIfStale(err, cwd);

    assertThat(errBuffer.toString(UTF_8)).isEmpty();
  }

  @Test
  void warnsWhenTheInstalledSkillDriftedFromTheBundledVersion() throws Exception {
    SkillInstaller.install(List.of("--skills"), out, err, cwd, home);
    Path skillMd = cwd.resolve(".claude/skills/selenide-cli/SKILL.md");
    Files.writeString(skillMd, Files.readString(skillMd, UTF_8) + "\nstale edit");

    SkillInstaller.warnIfStale(err, cwd);

    assertThat(errBuffer.toString(UTF_8))
      .contains("does not match the tool version")
      .contains("selenide install --skills")
      .contains(".claude/skills/selenide-cli");
  }
}
