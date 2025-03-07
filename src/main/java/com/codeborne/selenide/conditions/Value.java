package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.TextCheck;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

public class Value extends WebElementCondition {
  private final String expectedValue;

  public Value(String expectedValue) {
    super("value");
    this.expectedValue = expectedValue;
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String value = getValueAttribute(element);
    String actualValue = String.format("%s=\"%s\"", getName(), value);
    return new CheckResult(match(driver.config().textCheck(), value), actualValue);
  }

  private boolean match(TextCheck textCheck, String value) {
    return switch (textCheck) {
      case FULL_TEXT -> Html.text.equals(value, expectedValue);
      case PARTIAL_TEXT -> Html.text.contains(value, expectedValue);
    };
  }

  @Override
  public String toString() {
    return String.format("%s=\"%s\"", getName(), expectedValue);
  }

  private String getValueAttribute(WebElement element) {
    String attr = element.getAttribute("value");
    return attr == null ? "" : attr;
  }
}
