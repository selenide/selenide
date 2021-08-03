package com.codeborne.selenide.conditions.webdriver;

import org.openqa.selenium.WebDriver;

public class Url extends UrlCondition {
  public Url(String expectedUrl) {
    super("", expectedUrl);
  }

  @Override
  public boolean test(WebDriver webDriver) {
    return webDriver.getCurrentUrl().equals(expectedUrl);
  }
}
