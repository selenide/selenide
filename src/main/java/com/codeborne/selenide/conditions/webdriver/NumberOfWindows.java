package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class NumberOfWindows implements ObjectCondition<WebDriver> {
  private final int expectedNumberOfWindows;

  public NumberOfWindows(int expectedNumberOfWindows) {
    this.expectedNumberOfWindows = expectedNumberOfWindows;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String description() {
    return "should have " + expectedNumberOfWindows + " window(s)";
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String negativeDescription() {
    return "should not have " + expectedNumberOfWindows + " window(s)";
  }

  @CheckReturnValue
  @Override
  public CheckResult check(WebDriver webDriver) {
    int count = webDriver.getWindowHandles().size();
    return result(webDriver, count == expectedNumberOfWindows, String.valueOf(count));
  }

  @Override
  @Nullable
  @CheckReturnValue
  public String expectedValue() {
    return String.valueOf(expectedNumberOfWindows);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String describe(WebDriver webDriver) {
    return "webdriver";
  }
}
