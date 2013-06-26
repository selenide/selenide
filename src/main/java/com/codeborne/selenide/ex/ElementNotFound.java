package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.impl.WebElementsCollection;

import java.util.Arrays;

public class ElementNotFound extends AssertionError {
  public ElementNotFound(String searchCriteria, Condition expectedCondition, long timeoutMs) {
    super(searchCriteria +
        "\nExpected: " + expectedCondition +
        "\nTimeout: " + timeoutMs / 1000 + " s.");
  }

  public ElementNotFound(WebElementsCollection collection, String[] expectedTexts, long timeoutMs) {
    super(collection.description() +
        "\nExpected: " + Arrays.toString(expectedTexts) +
        "\nTimeout: " + timeoutMs / 1000 + " s.");
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + ' ' + getMessage();
  }
}
