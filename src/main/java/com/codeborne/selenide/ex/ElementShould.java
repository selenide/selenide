package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Context;
import com.codeborne.selenide.impl.Describe;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.ex.ErrorMessages.actualValue;

public class ElementShould extends UIAssertionError {
  public ElementShould(String searchCriteria, String prefix, Condition expectedCondition,
                       Context context, WebElement element, Exception lastError) {
    this(searchCriteria, prefix, null, expectedCondition, context, element, lastError);
  }

  public ElementShould(String searchCriteria, String prefix, String message, Condition expectedCondition,
                       Context context, WebElement element, Throwable lastError) {
    super("Element should " + prefix + expectedCondition + " {" + searchCriteria + "}" +
        (message != null ? " because " + message : "") +
        "\nElement: '" + Describe.describe(context, element) + '\'' +
        actualValue(expectedCondition, context, element), lastError);
  }

  @Override
  public String toString() {
    return getMessage() + uiDetails();
  }
}
