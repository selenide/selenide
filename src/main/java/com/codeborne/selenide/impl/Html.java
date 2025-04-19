package com.codeborne.selenide.impl;

import java.util.regex.Pattern;

import static java.util.Locale.ROOT;
import static java.util.regex.Pattern.DOTALL;
import static org.apache.commons.lang3.StringUtils.isEmpty;

public class Html {
  private static final Pattern REGEX_SPACES = Pattern.compile("[\\s\\p{Zs}\u200B\u200C\u200D\u2060]+");

  public static Html text = new Html();

  public boolean matches(String text, String regex) {
    return Pattern.compile(regex, DOTALL).matcher(text).matches();
  }

  public boolean matchesSubstring(String text, String regex) {
    return Pattern.compile(".*" + regex + ".*", DOTALL).matcher(text).matches();
  }

  public boolean contains(String text, String subtext) {
    String expected = reduceSpaces(subtext.toLowerCase(ROOT));
    verifyNotEmptySubstring(expected);
    return reduceSpaces(text.toLowerCase(ROOT)).contains(expected);
  }

  private static void verifyNotEmptySubstring(String expectedText) {
    if (isEmpty(expectedText)) {
      throw new IllegalArgumentException("Expected substring must not be null or empty string. " +
                                         "Consider setting Configuration.textCheck = FULL_TEXT;");
    }
  }

  public boolean containsCaseSensitive(String text, String subtext) {
    return reduceSpaces(text).contains(reduceSpaces(subtext));
  }

  public boolean equals(String text, String subtext) {
    return reduceSpaces(text).equalsIgnoreCase(reduceSpaces(subtext));
  }

  public boolean equalsCaseSensitive(String text, String subtext) {
    return reduceSpaces(text).equals(reduceSpaces(subtext));
  }

  public String reduceSpaces(String text) {
    return REGEX_SPACES.matcher(text).replaceAll(" ").trim();
  }
}
