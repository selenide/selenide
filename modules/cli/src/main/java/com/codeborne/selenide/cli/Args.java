package com.codeborne.selenide.cli;

import java.util.ArrayList;
import java.util.List;

import static java.util.Locale.ROOT;

/**
 * A parsed command line as a list of tokens.
 *
 * <p>Client invocations already arrive split by the shell, so {@link #ofTokens} is used on the
 * daemon side. {@link #ofLine} additionally tokenizes a raw string (whitespace separates tokens;
 * double quotes group a value that contains spaces, e.g. {@code setValue #q "hello world"}).
 */
final class Args {
  private final List<String> tokens;

  private Args(List<String> tokens) {
    this.tokens = tokens;
  }

  static Args ofTokens(List<String> tokens) {
    return new Args(List.copyOf(tokens));
  }

  static Args ofLine(String line) {
    return new Args(tokenize(line.strip()));
  }

  String command() {
    return tokens.isEmpty() ? "" : tokens.get(0).toLowerCase(ROOT);
  }

  int count() {
    return tokens.size();
  }

  String nth(int index) {
    if (index >= tokens.size()) {
      throw new CommandException("Missing argument #" + index);
    }
    return tokens.get(index);
  }

  String selector() {
    return nth(1);
  }

  /**
   * Everything after the selector (token #1), joined by single spaces.
   */
  String value() {
    return rest(2);
  }

  /**
   * The remaining tokens from index {@code from}, joined by single spaces.
   */
  String rest(int from) {
    if (from >= tokens.size()) {
      throw new CommandException("Missing argument");
    }
    return String.join(" ", tokens.subList(from, tokens.size()));
  }

  private static List<String> tokenize(String line) {
    List<String> result = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    boolean inQuotes = false;
    boolean started = false;
    for (int i = 0; i < line.length(); i++) {
      char c = line.charAt(i);
      if (c == '"') {
        inQuotes = !inQuotes;
        started = true;
      }
      else if (Character.isWhitespace(c) && !inQuotes) {
        started = flush(result, current, started);
      }
      else {
        current.append(c);
        started = true;
      }
    }
    flush(result, current, started);
    return result;
  }

  private static boolean flush(List<String> result, StringBuilder current, boolean started) {
    if (started) {
      result.add(current.toString());
      current.setLength(0);
    }
    return false;
  }
}
