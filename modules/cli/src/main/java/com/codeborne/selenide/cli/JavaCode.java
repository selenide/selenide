package com.codeborne.selenide.cli;

/**
 * Helpers for emitting Java source: static-import identifiers and escaping of string literals.
 */
final class JavaCode {
  static final String SELENIDE = "com.codeborne.selenide.Selenide.";
  static final String SELECTORS = "com.codeborne.selenide.Selectors.";

  static final String IMPORT_DOLLAR = SELENIDE + "$";
  static final String IMPORT_OPEN = SELENIDE + "open";
  static final String IMPORT_BACK = SELENIDE + "back";
  static final String IMPORT_FORWARD = SELENIDE + "forward";
  static final String IMPORT_REFRESH = SELENIDE + "refresh";
  static final String IMPORT_SCREENSHOT = SELENIDE + "screenshot";
  static final String IMPORT_SWITCHTO = SELENIDE + "switchTo";

  static final String IMPORT_BYTEXT = SELECTORS + "byText";
  static final String IMPORT_BYXPATH = SELECTORS + "byXpath";

  private JavaCode() {
  }

  /**
   * Renders the given text as a quoted, escaped Java string literal.
   */
  static String string(String raw) {
    StringBuilder sb = new StringBuilder(raw.length() + 2);
    sb.append('"');
    for (int i = 0; i < raw.length(); i++) {
      char c = raw.charAt(i);
      switch (c) {
        case '"' -> sb.append("\\\"");
        case '\\' -> sb.append("\\\\");
        case '\n' -> sb.append("\\n");
        case '\r' -> sb.append("\\r");
        case '\t' -> sb.append("\\t");
        default -> sb.append(c);
      }
    }
    sb.append('"');
    return sb.toString();
  }
}
