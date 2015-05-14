package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.By;

import java.util.Arrays;

public class ElementNotFound extends UIAssertionError {
  public ElementNotFound(By searchCriteria, Condition expectedCondition) {
    this(searchCriteria.toString(), expectedCondition, null);
  }

  public ElementNotFound(String searchCriteria, Condition expectedCondition, Throwable lastError) {
    super("Element not found {" + searchCriteria + '}' +
        "\nExpected: " + expectedCondition, lastError);
  }

  public ElementNotFound(WebElementsCollection collection, String[] expectedTexts, Throwable lastError) {
    super("Element not found {" + collection.description() + '}' +
        "\nExpected: " + Arrays.toString(expectedTexts), lastError);
  }

  @Override
  public String toString() {
    return getMessage();
  }
}
