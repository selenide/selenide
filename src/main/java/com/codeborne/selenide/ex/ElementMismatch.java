package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.impl.Describe;
import org.openqa.selenium.WebElement;

public class ElementMismatch extends AssertionError {
  public ElementMismatch(String searchCriteria, Condition expectedCondition, WebElement element, long timeoutMs) {
    super(searchCriteria +
        "\nExpected: " + expectedCondition +
        "\nElement: '" + Describe.describe(element) + "'" +
        "\nTimeout: " + timeoutMs/1000 + " s.");
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getMessage();
  }
}
