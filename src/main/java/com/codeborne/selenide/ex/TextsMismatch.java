package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;

import java.util.List;

import static java.lang.System.lineSeparator;

public class TextsMismatch extends UIAssertionError {
  public TextsMismatch(WebElementsCollection collection, List<String> actualTexts,
                       List<String> expectedTexts, String explanation, long timeoutMs) {
    super(collection.driver(),
      "Texts mismatch" +
        lineSeparator() + "Actual: " + actualTexts +
        lineSeparator() + "Expected: " + expectedTexts +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "Collection: " + collection.description());
    super.timeoutMs = timeoutMs;
  }
}
