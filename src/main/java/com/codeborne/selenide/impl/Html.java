package com.codeborne.selenide.impl;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.DOTALL;

public class Html {
  public static Html text = new Html();

  public boolean matches(String text, String regex) {
    return Pattern.compile(".*" + regex + ".*", DOTALL).matcher(text).matches();
  }

  public boolean contains(String text, String subtext) {
    return reduceSpaces(text.toLowerCase()).contains(reduceSpaces(subtext.toLowerCase()));
  }

  public boolean containsCaseSensitive(String text, String subtext) {
    return reduceSpaces(text).contains(reduceSpaces(subtext));
  }

  public boolean equals(String text, String subtext) {
    return reduceSpaces(text).equalsIgnoreCase(reduceSpaces(subtext.toLowerCase()));
  }

  public boolean equalsCaseSensitive(String text, String subtext) {
    return reduceSpaces(text).contains(reduceSpaces(subtext));
  }

  String reduceSpaces(String text) {
    return text.replaceAll("[\\s\\n\\r\u00a0]+", " ").trim();
  }
}
