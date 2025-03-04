package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.impl.JavaScript.jsExecutor;
import static java.util.Objects.requireNonNull;

public abstract class CurrentFrameCondition implements ObjectCondition<WebDriver> {
  private final String name;
  protected final String expectedUrl;

  protected CurrentFrameCondition(String name, String expectedUrl) {
    this.name = name;
    this.expectedUrl = expectedUrl;
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
  public String expectedValue() {
    return expectedUrl;
  }

  protected String getCurrentFrameUrl(WebDriver webDriver) {
    return requireNonNull(jsExecutor(webDriver).executeScript("return window.location.href")).toString();
  }

  @Override
  public String describe(WebDriver webDriver) {
    return "current frame";
  }

  @Override
  public CheckResult check(WebDriver webDriver) {
    String url = getCurrentFrameUrl(webDriver);
    return result(webDriver, test(url), url);
  }

  protected abstract boolean test(String url);
}
