package com.codeborne.selenide;

import java.util.Map;

import static com.codeborne.selenide.CaseSensitivity.CASE_INSENSITIVE;
import static com.codeborne.selenide.CaseSensitivity.CASE_SENSITIVE;
import static com.codeborne.selenide.TextCheck.FULL_TEXT;
import static com.codeborne.selenide.TextCheck.PARTIAL_TEXT;
import static com.codeborne.selenide.WhitespaceHandling.IGNORE_WHITESPACES;
import static com.codeborne.selenide.WhitespaceHandling.PRESERVE_WHITESPACES;
import static com.codeborne.selenide.WhitespaceHandling.TRIM_WHITESPACES;

public record TextMatchOptions(TextCheck textCheck, CaseSensitivity caseSensitivity, WhitespaceHandling whitespaceHandling) {
  public static TextMatchOptions fullText() {
    return new TextMatchOptions(FULL_TEXT, CASE_SENSITIVE, TRIM_WHITESPACES);
  }

  public static TextMatchOptions partialText() {
    return new TextMatchOptions(PARTIAL_TEXT, CASE_SENSITIVE, TRIM_WHITESPACES);
  }

  public TextMatchOptions caseInsensitive() {
    return new TextMatchOptions(textCheck, CASE_INSENSITIVE, whitespaceHandling);
  }

  public TextMatchOptions caseSensitive() {
    return new TextMatchOptions(textCheck, CASE_SENSITIVE, whitespaceHandling);
  }

  public TextMatchOptions preserveWhitespaces() {
    return new TextMatchOptions(textCheck, caseSensitivity, PRESERVE_WHITESPACES);
  }

  public TextMatchOptions ignoreWhitespaces() {
    return new TextMatchOptions(textCheck, caseSensitivity, IGNORE_WHITESPACES);
  }

  public TextMatchOptions trimWhitespaces() {
    return new TextMatchOptions(textCheck, caseSensitivity, TRIM_WHITESPACES);
  }

  public Map<String, String> toMap() {
    return Map.of(
      "textCheck", textCheck.name(),
      "caseSensitivity", caseSensitivity.name(),
      "whitespaceHandling", whitespaceHandling.name()
    );
  }
}
