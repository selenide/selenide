package com.codeborne.selenide.impl;

import java.util.regex.Pattern;

import static java.util.Locale.ROOT;
import static java.util.regex.Pattern.DOTALL;

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
    return reduceSpaces(text.toLowerCase(ROOT)).contains(reduceSpaces(subtext.toLowerCase(ROOT)));
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
