package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

public class CookieWithName implements ObjectCondition<WebDriver> {

  private final String expectedName;

  public CookieWithName(String expectedName) {
    this.expectedName = expectedName;
  }

  @Override
  public String description() {
    return String.format("should have a cookie with name \"%s\"", expectedName);
  }

  @Override
  public String negativeDescription() {
    return String.format("should not have cookie with name \"%s\"", expectedName);
  }

  @Override
  public CheckResult check(WebDriver webDriver) {
    Cookie cookie = webDriver.manage().getCookieNamed(expectedName);
    return result(webDriver, cookie != null, actualValue(webDriver));
  }

  private String actualValue(WebDriver webDriver) {
    return String.format("Available cookies: %s", webDriver.manage().getCookies());
  }

  @Override
  public String expectedValue() {
    return String.format("cookie with name \"%s\"", expectedName);
  }

  @Override
  public String describe(WebDriver webDriver) {
    return "webdriver";
  }
}
