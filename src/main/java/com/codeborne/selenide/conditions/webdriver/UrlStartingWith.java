package com.codeborne.selenide.conditions.webdriver;

import org.openqa.selenium.WebDriver;

public class UrlStartingWith extends UrlCondition {
  public UrlStartingWith(String expectedUrl) {
    super("starting with ", expectedUrl);
  }

  @Override
  public boolean test(WebDriver webDriver) {
    return webDriver.getCurrentUrl().startsWith(expectedUrl);
  }
}
