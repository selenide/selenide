package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.By;

import java.util.List;

public class ElementNotFound extends UIAssertionError {
  public ElementNotFound(Driver driver, By searchCriteria, Condition expectedCondition) {
    this(driver, searchCriteria.toString(), expectedCondition, null);
  }

  public ElementNotFound(Driver driver, String searchCriteria, Condition expectedCondition) {
    super(driver,
      "Element not found {" + searchCriteria + '}' +
        "\nExpected: " + expectedCondition);
  }

  public ElementNotFound(Driver driver, String searchCriteria, Condition expectedCondition, Throwable lastError) {
    super(driver,
      "Element not found {" + searchCriteria + '}' +
        "\nExpected: " + expectedCondition, lastError);
  }

  public ElementNotFound(WebElementsCollection collection, List<String> expectedTexts, Throwable lastError) {
    super(collection.driver(),
      "Element not found {" + collection.description() + '}' +
        "\nExpected: " + expectedTexts, lastError);
  }

  @Override
  public String toString() {
    return getMessage() + uiDetails();
  }
}
