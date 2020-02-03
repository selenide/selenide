package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;

import java.util.List;

public class TextsSizeMismatch extends UIAssertionError {
  public TextsSizeMismatch(WebElementsCollection collection, List<String> actualTexts,
                       List<String> expectedTexts, String explanation, long timeoutMs) {
    super(collection.driver(),
        "Texts size mismatch" +
        "\nActual: " + actualTexts + ", List size: " + actualTexts.size() +
        "\nExpected: " + expectedTexts + ", List size: " + expectedTexts.size() +
        (explanation == null ? "" : "\nBecause: " + explanation) +
        "\nCollection: " + collection.description());
    super.timeoutMs = timeoutMs;
  }
}
