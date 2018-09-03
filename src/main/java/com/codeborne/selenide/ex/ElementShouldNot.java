package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Context;
import com.codeborne.selenide.impl.Describe;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.ex.ErrorMessages.actualValue;

public class ElementShouldNot extends UIAssertionError {
  public ElementShouldNot(String searchCriteria, String prefix, String message, Condition expectedCondition,
                          Context context, WebElement element, Throwable lastError) {
    super("Element should not " + prefix + expectedCondition + " {" + searchCriteria + '}' +
        (message != null ? " because " + message : "") +
        "\nElement: '" + Describe.describe(context, element) + '\'' +
        actualValue(expectedCondition, context, element), lastError);
  }

  @Override
  public String toString() {
    return getMessage() + uiDetails();
  }
}
