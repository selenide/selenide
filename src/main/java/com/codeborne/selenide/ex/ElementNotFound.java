package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.By;

import java.util.Arrays;

public class ElementNotFound extends UIAssertionError {
  public ElementNotFound(By searchCriteria, Condition expectedCondition, long timeoutMs) {
    this(searchCriteria.toString(), expectedCondition, timeoutMs);
  }

  public ElementNotFound(String searchCriteria, Condition expectedCondition, long timeoutMs) {
    super("Element not found {" + searchCriteria + '}' +
        "\nExpected: " + expectedCondition, timeoutMs);
  }

  public ElementNotFound(WebElementsCollection collection, String[] expectedTexts, long timeoutMs) {
    super("Element not found {" + collection.description() + '}' +
        "\nExpected: " + Arrays.toString(expectedTexts), timeoutMs);
  }

  @Override
  public String toString() {
    return getMessage();
  }
}
