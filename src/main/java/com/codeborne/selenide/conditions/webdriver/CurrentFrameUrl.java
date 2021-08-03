package com.codeborne.selenide.conditions.webdriver;

import org.openqa.selenium.WebDriver;

public class CurrentFrameUrl extends CurrentFrameCondition {
  public CurrentFrameUrl(String expectedUrl) {
    super("", expectedUrl);
  }

  @Override
  public boolean test(WebDriver webDriver) {
    return getCurrentFrameUrl(webDriver).equals(expectedUrl);
  }
}
