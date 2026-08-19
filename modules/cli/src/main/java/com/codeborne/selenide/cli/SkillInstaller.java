package com.codeborne.selenide.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Installs the bundled {@code selenide-cli} Agent Skill (SKILL.md + references, embedded on the
 * classpath at build time from {@code modules/cli/skills/}) into a project's - or the user's home -
 * {@code .claude/skills/} or {@code .agents/skills/} directory, and warns when an already-installed
 * copy has drifted from the version bundled in the currently running CLI. Mirrors the
 * {@code playwright-cli install --skills[=agents]} convention.
 */
final class SkillInstaller {
  private static final String SKILL_NAME = "selenide-cli";
  private static final String SKILL_MD = "SKILL.md";
  private static final List<String> AGENT_DIRS = List.of("claude", "agents");
  private static final List<String> SKILL_FILES = List.of(
    SKILL_MD,
    "references/build-and-run.md",
    "references/codegen.md",
    "references/commands.md",
    "references/configuration.md",
    "references/scripting.md",
    "references/selectors.md");

  private SkillInstaller() {
  }

  static int install(List<String> args, PrintStream out, PrintStream err) {
    return install(args, out, err, cwd(), homeDir());
  }

  static int install(List<String> args, PrintStream out, PrintStream err, Path cwd, Path home) {
    Flags flags = Flags.parse(args);
    if (flags.skills() == null) {
      err.println("Usage: selenide install --skills[=agents] [--global]");
      return 1;
    }
    if (!AGENT_DIRS.contains(flags.skills())) {
      err.println("Unknown --skills value: '" + flags.skills() + "'. Use --skills or --skills=agents.");
      return 1;
    }
    Path base = flags.global() ? home : cwd;
    Path destDir = base.resolve("." + flags.skills()).resolve("skills").resolve(SKILL_NAME);
    try {
      copySkill(destDir);
    }
    catch (IOException e) {
      err.println("Could not install skill: " + e.getMessage());
      return 1;
    }
    out.println("Installed " + SKILL_NAME + " skill to " + (flags.global() ? destDir : cwd.relativize(destDir)));
    return 0;
  }

  static void warnIfStale(PrintStream err) {
    warnIfStale(err, cwd(), homeDir());
  }

  static void warnIfStale(PrintStream err, Path cwd, Path home) {
    for (String agent : AGENT_DIRS) {
      warnIfStaleAt(err, cwd, agent, false);
      warnIfStaleAt(err, home, agent, true);
    }
  }

  private static void warnIfStaleAt(PrintStream err, Path base, String agent, boolean global) {
    Path installedDir = base.resolve("." + agent).resolve("skills").resolve(SKILL_NAME);
    if (!hasDrifted(installedDir)) {
      return;
    }
    Path shown = global ? installedDir : base.relativize(installedDir);
    err.print(staleWarning(shown, agent, global));
  }

  /** True if {@code installedDir} holds a copy of the skill and any of its files differs from the bundled one. */
  private static boolean hasDrifted(Path installedDir) {
    if (!Files.isRegularFile(installedDir.resolve(SKILL_MD))) {
      return false;
    }
    for (String relativePath : SKILL_FILES) {
      String bundled = readBundledFile(relativePath);
      String installed = readFile(installedDir.resolve(relativePath));
      if (bundled == null || !bundled.equals(installed)) {
        return true;
      }
    }
    return false;
  }

  private static String staleWarning(Path shown, String agent, boolean global) {
    String installCommand = "selenide install --skills" + (agent.equals("agents") ? "=agents" : "") + (global ? " --global" : "");
    return frame(List.of(
      "The " + SKILL_NAME + " skill at '" + shown + "'",
      "does not match the tool version.",
      "",
      "Run `" + installCommand + "`",
      "to install the up-to-date skill."));
  }

  private static void copySkill(Path destDir) throws IOException {
    for (String relativePath : SKILL_FILES) {
      Path dest = destDir.resolve(relativePath);
      Files.createDirectories(dest.getParent());
      try (InputStream in = bundledResource(relativePath)) {
        Files.copy(in, dest, StandardCopyOption.REPLACE_EXISTING);
      }
    }
  }

  private static InputStream bundledResource(String relativePath) throws IOException {
    InputStream in = SkillInstaller.class.getResourceAsStream("/" + SKILL_NAME + "/" + relativePath);
    if (in == null) {
      throw new IOException("bundled skill resource not found: " + relativePath);
    }
    return in;
  }

  private static String readBundledFile(String relativePath) {
    try (InputStream in = bundledResource(relativePath)) {
      return normalize(new String(in.readAllBytes(), UTF_8));
    }
    catch (IOException e) {
      return null;
    }
  }

  private static String readFile(Path file) {
    if (!Files.isRegularFile(file)) {
      return null;
    }
    try {
      return normalize(Files.readString(file, UTF_8));
    }
    catch (IOException e) {
      return null;
    }
  }

  private static String normalize(String text) {
    return text.replace("\r\n", "\n");
  }

  private static String frame(List<String> lines) {
    int width = lines.stream().mapToInt(String::length).max().orElse(0);
    String horizontal = "═".repeat(width + 2);
    StringBuilder result = new StringBuilder();
    result.append('╔').append(horizontal).append('╗').append('\n');
    for (String line : lines) {
      result.append("║ ").append(line).append(" ".repeat(width - line.length())).append(" ║").append('\n');
    }
    result.append('╚').append(horizontal).append('╝').append('\n');
    return result.toString();
  }

  private static Path cwd() {
    return Path.of("").toAbsolutePath();
  }

  private static Path homeDir() {
    return Path.of(System.getProperty("user.home"));
  }

  private record Flags(String skills, boolean global) {
    static Flags parse(List<String> args) {
      String skills = null;
      boolean global = false;
      for (String arg : args) {
        if (arg.equals("--skills")) {
          skills = "claude";
        }
        else if (arg.startsWith("--skills=")) {
          skills = arg.substring("--skills=".length());
        }
        else if (arg.equals("--global") || arg.equals("-g")) {
          global = true;
        }
      }
      return new Flags(skills, global);
    }
  }
}
