package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;

import java.util.List;

import static java.lang.System.lineSeparator;

public class TextsSizeMismatch extends UIAssertionError {
  public TextsSizeMismatch(WebElementsCollection collection, List<String> actualTexts,
                       List<String> expectedTexts, String explanation, long timeoutMs) {
    super(collection.driver(),
      "Texts size mismatch" +
        lineSeparator() + "Actual: " + actualTexts + ", List size: " + actualTexts.size() +
        lineSeparator() + "Expected: " + expectedTexts + ", List size: " + expectedTexts.size() +
        (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
        lineSeparator() + "Collection: " + collection.description()
    );
    super.timeoutMs = timeoutMs;
  }
}
