package com.codeborne.selenide.impl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.regex.Pattern;

import static java.util.Locale.ROOT;
import static java.util.regex.Pattern.DOTALL;

@ParametersAreNonnullByDefault
public class Html {
  private static final Pattern REGEX_SPACES = Pattern.compile(
    "[\\s\\p{Zs}\u1680\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u200B\u200C\u200D\u202F\u205F\u2060\u3000]+");

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
