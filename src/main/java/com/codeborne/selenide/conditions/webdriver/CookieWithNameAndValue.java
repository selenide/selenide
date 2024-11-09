package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.util.Objects;

public class CookieWithNameAndValue implements ObjectCondition<WebDriver> {

  private final String name;
  private final String value;

  public CookieWithNameAndValue(String name, String value) {
    this.name = name;
    this.value = value;
  }

  @Override
  public String description() {
    return String.format("should have a cookie with name \"%s\" and value \"%s\"", name, value);
  }

  @Override
  public String negativeDescription() {
    return String.format("should not have cookie with name \"%s\" and value \"%s\"", name, value);
  }

  @Override
  public CheckResult check(WebDriver webDriver) {
    Cookie cookie = webDriver.manage().getCookieNamed(name);
    boolean met = cookie != null && Objects.equals(cookie.getValue(), value);
    return result(webDriver, met, actualValue(webDriver));
  }

  private String actualValue(WebDriver webDriver) {
    return String.format("Available cookies: %s", webDriver.manage().getCookies());
  }

  @Override
  public String expectedValue() {
    return String.format("cookie with name \"%s\" and value \"%s\"", name, value);
  }

  @Override
  public String describe(WebDriver webDriver) {
    return "webdriver";
  }
}
