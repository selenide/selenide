package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NumberOfTabs implements ObjectCondition<WebDriver> {
  private final int expectedNumberOfTabs;

  public NumberOfTabs(int expectedNumberOfTabs) {
    this.expectedNumberOfTabs = expectedNumberOfTabs;
  }

  @Nonnull
  @Override
  public String description() {
    return "should have " + expectedNumberOfTabs + " tab(s)";
  }

  @Nonnull
  @Override
  public String negativeDescription() {
    return "should not have " + expectedNumberOfTabs + " tab(s)";
  }

  @Override
  public boolean test(WebDriver webDriver) {
    return webDriver.getWindowHandles().size() == expectedNumberOfTabs;
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
