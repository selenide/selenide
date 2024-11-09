package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

public class NumberOfWindows implements ObjectCondition<WebDriver> {
  private final int expectedNumberOfWindows;

  public NumberOfWindows(int expectedNumberOfWindows) {
    this.expectedNumberOfWindows = expectedNumberOfWindows;
  }

  @Override
  public String description() {
    return "should have " + expectedNumberOfWindows + " window(s)";
  }

  @Override
  public String negativeDescription() {
    return "should not have " + expectedNumberOfWindows + " window(s)";
  }

  @Override
  public CheckResult check(WebDriver webDriver) {
    int count = webDriver.getWindowHandles().size();
    return result(webDriver, count == expectedNumberOfWindows, String.valueOf(count));
  }

  @Override
  public String expectedValue() {
    return String.valueOf(expectedNumberOfWindows);
  }

  @Override
  public String describe(WebDriver webDriver) {
    return "webdriver";
  }
}
