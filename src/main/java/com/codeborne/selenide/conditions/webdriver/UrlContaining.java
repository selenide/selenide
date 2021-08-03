package com.codeborne.selenide.conditions.webdriver;

import org.openqa.selenium.WebDriver;

public class UrlContaining extends UrlCondition {
  public UrlContaining(String expectedUrl) {
    super("containing ", expectedUrl);
  }

  @Override
  public boolean test(WebDriver webDriver) {
    return webDriver.getCurrentUrl().contains(expectedUrl);
  }
}
