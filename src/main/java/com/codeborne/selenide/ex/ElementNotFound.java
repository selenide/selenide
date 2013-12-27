package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.impl.WebElementsCollection;

import java.util.Arrays;

import static com.codeborne.selenide.ex.ErrorMessages.timeout;

public class ElementNotFound extends AssertionError {
  public ElementNotFound(String searchCriteria, Condition expectedCondition, long timeoutMs, String screenshot) {
    super("Element not found {" + searchCriteria + '}' +
        "\nExpected: " + expectedCondition +
        "\nScreenshot: " + screenshot +
        timeout(timeoutMs));
  }

  public ElementNotFound(WebElementsCollection collection, String[] expectedTexts, long timeoutMs, String screenshot) {
    super("Element not found {" + collection.description() + '}' +
        "\nExpected: " + Arrays.toString(expectedTexts) +
        "\nScreenshot: " + screenshot +
        timeout(timeoutMs));
  }

  @Override
  public String toString() {
    return getMessage();
  }
}
