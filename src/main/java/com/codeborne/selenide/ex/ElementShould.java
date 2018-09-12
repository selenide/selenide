package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Describe;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.ex.ErrorMessages.actualValue;

public class ElementShould extends UIAssertionError {
  public ElementShould(String searchCriteria, String prefix, Condition expectedCondition,
                       Driver driver, WebElement element, Exception lastError) {
    this(searchCriteria, prefix, null, expectedCondition, driver, element, lastError);
  }

  public ElementShould(String searchCriteria, String prefix, String message, Condition expectedCondition,
                       Driver driver, WebElement element, Throwable lastError) {
    super("Element should " + prefix + expectedCondition + " {" + searchCriteria + "}" +
        (message != null ? " because " + message : "") +
        "\nElement: '" + Describe.describe(driver, element) + '\'' +
        actualValue(expectedCondition, driver, element), lastError);
  }

  @Override
  public String toString() {
    return getMessage() + uiDetails();
  }
}
