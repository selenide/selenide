package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;

import java.util.List;

public class TextsMismatch extends UIAssertionError {
  public TextsMismatch(WebElementsCollection collection, List<String> actualTexts,
                       List<String> expectedTexts, String explanation, long timeoutMs) {
    super(collection.driver(),
      "\nActual: " + actualTexts +
        "\nExpected: " + expectedTexts +
        (explanation == null ? "" : "\nBecause: " + explanation) +
        "\nCollection: " + collection.description());
    super.timeoutMs = timeoutMs;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + ' ' + getMessage() + uiDetails();
  }
}
