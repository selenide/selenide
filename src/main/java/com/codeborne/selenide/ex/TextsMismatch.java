package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;

import java.util.Arrays;

public class TextsMismatch extends UIAssertionError {
  public TextsMismatch(WebElementsCollection collection, String[] actualTexts,
                       String[] expectedTexts, long timeoutMs) {
    super("\nActual: " + Arrays.toString(actualTexts) +
        "\nExpected: " + Arrays.toString(expectedTexts) +
        "\nCollection: " + collection.description(), timeoutMs);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + ' ' + getMessage();
  }
}
