package com.codeborne.selenide.conditions.webdriver;

import org.openqa.selenium.WebDriver;

public class CurrentFrameUrlStartingWith extends CurrentFrameCondition {
  public CurrentFrameUrlStartingWith(String expectedUrl) {
    super("starting with ", expectedUrl);
  }

  @Override
  public boolean test(WebDriver webDriver) {
    return getCurrentFrameUrl(webDriver).startsWith(expectedUrl);
  }
}
