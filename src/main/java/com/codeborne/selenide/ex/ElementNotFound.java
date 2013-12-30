package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.openqa.selenium.By;

import java.util.Arrays;

import static com.codeborne.selenide.ex.ErrorMessages.screenshot;
import static com.codeborne.selenide.ex.ErrorMessages.timeout;

public class ElementNotFound extends AssertionError {
  public ElementNotFound(By searchCriteria, Condition expectedCondition, long timeoutMs) {
    this(searchCriteria.toString(), expectedCondition, timeoutMs);
  }

  public ElementNotFound(String searchCriteria, Condition expectedCondition, long timeoutMs) {
    super("Element not found {" + searchCriteria + '}' +
        "\nExpected: " + expectedCondition +
        screenshot() +
        timeout(timeoutMs));
  }

  public ElementNotFound(WebElementsCollection collection, String[] expectedTexts, long timeoutMs) {
    super("Element not found {" + collection.description() + '}' +
        "\nExpected: " + Arrays.toString(expectedTexts) +
        screenshot() +
        timeout(timeoutMs));
  }

  @Override
  public String toString() {
    return getMessage();
  }
}
