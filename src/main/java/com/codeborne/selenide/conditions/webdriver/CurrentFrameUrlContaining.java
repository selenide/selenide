package com.codeborne.selenide.conditions.webdriver;

import org.openqa.selenium.WebDriver;

public class CurrentFrameUrlContaining extends CurrentFrameCondition {
  public CurrentFrameUrlContaining(String expectedUrl) {
    super("containing ", expectedUrl);
  }

  @Override
  public boolean test(WebDriver webDriver) {
    return getCurrentFrameUrl(webDriver).contains(expectedUrl);
  }
}
