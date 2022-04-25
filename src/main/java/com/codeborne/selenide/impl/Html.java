package com.codeborne.selenide.impl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.DOTALL;

@ParametersAreNonnullByDefault
public class Html {
  private static final Pattern REGEX_SPACES = Pattern.compile("[\\s\\n\\r\u00a0]+");
  public static Html text = new Html();

  public boolean matches(String text, String regex) {
    return Pattern.compile(regex, DOTALL).matcher(text).matches();
  }

  public boolean matchesSubstring(String text, String regex) {
    return Pattern.compile(".*" + regex + ".*", DOTALL).matcher(text).matches();
  }

  public boolean contains(String text, String subtext) {
    return reduceSpaces(text.toLowerCase(Locale.ROOT)).contains(reduceSpaces(subtext.toLowerCase(Locale.ROOT)));
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

  String reduceSpaces(String text) {
    return REGEX_SPACES.matcher(text).replaceAll(" ").trim();
  }
}
