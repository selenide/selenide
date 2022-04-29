package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
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

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    String actualCssValue = element.getCssValue(propertyName);
    boolean matches = defaultString(expectedValue).equalsIgnoreCase(defaultString(actualCssValue));
    return new CheckResult(matches, String.format("%s=%s", propertyName, actualCssValue));
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return getName() + " " + propertyName + '=' + expectedValue;
  }
}
