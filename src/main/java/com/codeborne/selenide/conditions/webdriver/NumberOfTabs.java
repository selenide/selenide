package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NumberOfWindows implements ObjectCondition<WebDriver> {
  private final int expectedNumberOfWindows;

  public NumberOfWindows(int expectedNumberOfWindows) {
    this.expectedNumberOfWindows = expectedNumberOfWindows;
  }

  @Nonnull
  @Override
  public String description() {
    return "should have number of windows = " + expectedNumberOfWindows;
  }

  @Nonnull
  @Override
  public String negativeDescription() {
    return "should not have number of windows = " + expectedNumberOfWindows;
  }

  @Override
  public boolean test(WebDriver webDriver) {
    return webDriver.getWindowHandles().size() == expectedNumberOfWindows;
  }

  @Nullable
  @Override
  public String actualValue(WebDriver webDriver) {
    return String.valueOf(webDriver.getWindowHandles().size());
  }

  @Nonnull
  @Override
  public String describe(WebDriver webDriver) {
    return "webdriver";
  }
}

