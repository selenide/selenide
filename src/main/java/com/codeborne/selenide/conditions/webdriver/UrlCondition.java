package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;

public abstract class UrlCondition implements ObjectCondition<WebDriver> {
  private final String name;
  protected final String expectedUrl;

  protected UrlCondition(String name, String expectedUrl) {
    this.name = name;
    this.expectedUrl = expectedUrl;
  }

  @Override
  public String expectedValue() {
    return expectedUrl;
  }

  @Override
  public String description() {
    return "should have url " + name + expectedUrl;
  }

  @Override
  public String negativeDescription() {
    return "should not have url " + name + expectedUrl;
  }

  @Override
  public String describe(WebDriver webDriver) {
    return "webdriver";
  }

  @Override
  public CheckResult check(WebDriver webDriver) {
    String url = webDriver.getCurrentUrl();
    return result(webDriver, test(url), url);
  }

  protected abstract boolean test(@Nullable String url);
}
