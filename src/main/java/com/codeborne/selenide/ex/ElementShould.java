package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Describe;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.ex.ErrorMessages.actualValue;

public class ElementShould extends UIAssertionError {
  public ElementShould(Driver driver, String searchCriteria, String prefix, Condition expectedCondition,
                       WebElement element, Throwable lastError) {
    super(driver,
      "Element should " + prefix + expectedCondition + " {" + searchCriteria + "}" +
        "\nElement: '" + Describe.describe(driver, element) + "'" +
        actualValue(expectedCondition, driver, element), lastError);
  }

  @Override
  public String toString() {
    return getMessage() + uiDetails();
  }
}
