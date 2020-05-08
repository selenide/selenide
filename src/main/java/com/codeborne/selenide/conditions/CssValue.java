package com.codeborne.selenide.conditions;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.apache.commons.lang3.StringUtils.defaultString;

@ParametersAreNonnullByDefault
public class CssValue extends Condition {
  private final String propertyName;
  private final String expectedValue;

  public CssValue(String propertyName, @Nullable String expectedValue) {
    super("css value");
    this.propertyName = propertyName;
    this.expectedValue = expectedValue;
  }

  @Override
  public boolean apply(Driver driver, WebElement element) {
    String actualCssValue = element.getCssValue(propertyName);
    return defaultString(expectedValue).equalsIgnoreCase(defaultString(actualCssValue));
  }

  @Override
  public String actualValue(Driver driver, WebElement element) {
    return element.getCssValue(propertyName);
  }

  @Override
  public String toString() {
    return getName() + " " + propertyName + '=' + expectedValue;
  }
}
