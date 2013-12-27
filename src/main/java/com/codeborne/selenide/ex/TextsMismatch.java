package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.WebElementsCollection;

import java.util.Arrays;

import static com.codeborne.selenide.ex.ErrorMessages.timeout;

public class TextsMismatch extends AssertionError {
  public TextsMismatch(WebElementsCollection collection, String[] actualTexts,
                       String[] expectedTexts, long timeoutMs, String screenshot) {
    super("\nActual: " + Arrays.toString(actualTexts) +
        "\nExpected: " + Arrays.toString(expectedTexts) +
        "\nCollection: " + collection.description() +
        "\nScreenshot: " + screenshot +
        timeout(timeoutMs));
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + ' ' + getMessage();
  }
}
