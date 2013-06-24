package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebElementsCollection;

import java.util.Arrays;

public class TextsNotMatch extends AssertionError {
  public TextsNotMatch(WebElementsCollection collection, String[] actualTexts, String[] expectedTexts, long timeoutMs) {
    super("\nActual: " + Arrays.toString(actualTexts) +
        "\nExpected: " + Arrays.toString(expectedTexts) +
        "\nCollection: " + collection.description() +
        "\nTimeout: " + timeoutMs / 1000 + " s.");
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + ' ' + getMessage();
  }
}
