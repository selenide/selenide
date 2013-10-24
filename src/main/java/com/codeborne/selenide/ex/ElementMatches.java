package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.impl.Describe;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.ex.ErrorMessages.timeout;

public class ElementMatches extends AssertionError {
  public ElementMatches(String searchCriteria, Condition expectedCondition, WebElement element, long timeoutMs) {
    super(searchCriteria +
        "\nExpected: " + expectedCondition +
        "\nElement: '" + Describe.describe(element) + '\'' +
        timeout(timeoutMs));
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + ' ' + getMessage();
  }
}
