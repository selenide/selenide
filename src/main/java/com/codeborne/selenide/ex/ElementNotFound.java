package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.impl.WebElementsCollection;

import java.util.Arrays;

import static com.codeborne.selenide.ex.ErrorMessages.timeout;

public class ElementNotFound extends AssertionError {
  public ElementNotFound(String searchCriteria, Condition expectedCondition, long timeoutMs) {
    super(searchCriteria +
        "\nExpected: " + expectedCondition +
        timeout(timeoutMs));
  }

  public ElementNotFound(WebElementsCollection collection, String[] expectedTexts, long timeoutMs) {
    super(collection.description() +
        "\nExpected: " + Arrays.toString(expectedTexts) +
        timeout(timeoutMs));
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + ' ' + getMessage();
  }
}
