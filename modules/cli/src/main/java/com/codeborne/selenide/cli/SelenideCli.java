package com.codeborne.selenide.cli;

import com.codeborne.selenide.SelenideConfig;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Command-line entry point for Selenide — a stateless, per-invocation CLI over a background daemon.
 *
 * <p>{@code selenide open <url>} starts (or reuses) a browser daemon for the session; subsequent
 * invocations like {@code selenide click "#submit"} run one command against that daemon. Each
 * successful command is recorded, and {@code selenide code} prints the generated Selenide Java.
 * Use {@code -s <name>} / {@code --session <name>} to run isolated sessions concurrently.
 */
public final class SelenideCli {
  private static final List<String> USAGE = List.of(
    "Selenide CLI — browser automation & Selenide codegen over a background daemon.",
    "",
    "Usage:",
    "  selenide [-s <session>] <command> [args]",
    "",
    "Lifecycle:",
    "  open [options] <url>     start/reuse a browser daemon and navigate to <url>",
    "  close                    close the session's browser and stop its daemon",
    "  list                     list running sessions",
    "  close-all                close every session",
    "",
    "Actions (recorded):  click, setValue, type, append, clear, selectOption, selectRadio,",
    "  check, uncheck, setSelected, hover, doubleClick, contextClick, scrollTo,",
    "  pressEnter, pressTab, pressEscape, should, back, forward, refresh, screenshot,",
    "  frame, defaultContent",
    "  e.g.  selenide click \"#submit\"   selenide setValue \"#email\" \"a@b.com\"",
    "        selenide should \"#msg\" text Welcome",
    "",
    "Codegen:  code (print generated Java), save <file>, undo, reset",
    "",
    "open options:  --browser=<name> --headless --browser-size=<WxH> --base-url=<url>",
    "  --timeout=<ms> --remote=<url> --reports-folder=<dir>  (see references/configuration)",
    "",
    "  -s, --session <name>     use a named session (default: \"default\")",
    "  -v, --version            print version",
    "  -h, --help               print this help");

  /** Commands handled daemon-side (via {@link SelenideExecutor}) but outside {@link CommandInterpreter}. */
  private static final Set<String> META_COMMANDS = Set.of("close", "code", "save", "undo", "reset");

  private SelenideCli() {
  }

  public static void main(String[] args) {
    System.exit(run(new ArrayList<>(Arrays.asList(args)), System.out, System.err));
  }

  public static int run(List<String> args, PrintStream out, PrintStream err) {
    String session = extractSession(args);
    if (args.isEmpty() || isHelp(args.get(0))) {
      USAGE.forEach(out::println);
      return 0;
    }
    String command = args.get(0);
    List<String> rest = args.subList(1, args.size());
    if (hasHelpFlag(rest)) {
      USAGE.forEach(out::println);
      return 0;
    }
    return switch (command) {
      case "__daemon" -> {
        runDaemon(session, rest);
        yield 0;
      }
      case "--version", "-v", "version" -> {
        out.println("selenide-cli " + version());
        yield 0;
      }
      case "open" -> new DaemonClient(session, out, err).open(rest);
      case "list" -> {
        new DaemonClient(session, out, err).list();
        yield 0;
      }
      case "close-all" -> new DaemonClient(session, out, err).closeAll();
      default -> {
        if (!isKnownCommand(command)) {
          err.println("Unknown command: '" + command + "'. Run 'selenide --help' to see commands.");
          yield 1;
        }
        yield new DaemonClient(session, out, err).command(args);
      }
    };
  }

  private static boolean hasHelpFlag(List<String> args) {
    return args.contains("--help") || args.contains("-h");
  }

  private static boolean isKnownCommand(String command) {
    String normalized = command.toLowerCase(Locale.ROOT);
    return META_COMMANDS.contains(normalized) || CommandInterpreter.commandNames().contains(normalized);
  }

  private static void runDaemon(String session, List<String> configFlags) {
    SelenideConfig config = CliConfig.toConfig(configFlags.toArray(new String[0]));
    Daemon.run(session, new SelenideExecutor(config));
  }

  private static String extractSession(List<String> args) {
    for (int i = 0; i < args.size(); i++) {
      String arg = args.get(i);
      if (arg.startsWith("-s=")) {
        args.remove(i);
        return arg.substring("-s=".length());
      }
      if (arg.startsWith("--session=")) {
        args.remove(i);
        return arg.substring("--session=".length());
      }
      if ((arg.equals("-s") || arg.equals("--session")) && i + 1 < args.size()) {
        String value = args.get(i + 1);
        args.remove(i + 1);
        args.remove(i);
        return value;
      }
    }
    return "default";
  }

  private static boolean isHelp(String arg) {
    return arg.equals("--help") || arg.equals("-h") || arg.equals("help");
  }

  /**
   * The set of commands this CLI understands (lower-case). Exposed for the MCP/CLI parity test so it
   * can verify both front-ends cover the shared browser-action catalog.
   */
  public static Set<String> commandNames() {
    return CommandInterpreter.commandNames();
  }

  static String version() {
    String version = SelenideCli.class.getPackage().getImplementationVersion();
    return version != null ? version : "dev";
  }
}
