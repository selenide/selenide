package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Html;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Value extends Condition {
  private final String expectedValue;

  public Value(String expectedValue) {
    super("value");
    this.expectedValue = expectedValue;
  }

  @Nonnull
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String value = getValueAttribute(element);
    return new CheckResult(Html.text.contains(value, expectedValue), value);
  }

  @Override
  public String toString() {
    return getName() + " '" + expectedValue + "'";
  }

  private String getValueAttribute(WebElement element) {
    String attr = element.getAttribute("value");
    return attr == null ? "" : attr;
  }
}
