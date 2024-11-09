package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import static org.apache.commons.lang3.StringUtils.defaultString;

public class CssValue extends WebElementCondition {
  private final String propertyName;

  @Nullable
  private final String expectedValue;

  public CssValue(String propertyName, @Nullable String expectedValue) {
    super("css value");
    this.propertyName = propertyName;
    this.expectedValue = expectedValue;
  }

  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String actualCssValue = element.getCssValue(propertyName);
    boolean matches = defaultString(expectedValue).equalsIgnoreCase(defaultString(actualCssValue));
    return new CheckResult(matches, String.format("%s=%s", propertyName, actualCssValue));
  }

  @Override
  public String toString() {
    return getName() + " " + propertyName + '=' + expectedValue;
  }
}
