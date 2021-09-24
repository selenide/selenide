package com.codeborne.selenide.conditions.webdriver;

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
  public boolean test(WebDriver webDriver) {
    return webDriver.getWindowHandles().size() == expectedNumberOfWindows;
  }

  @Nullable
  @CheckReturnValue
  @Override
  public String actualValue(WebDriver webDriver) {
    return String.valueOf(webDriver.getWindowHandles().size());
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
