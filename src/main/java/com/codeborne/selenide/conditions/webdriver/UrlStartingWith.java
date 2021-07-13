package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UrlStartingWith implements ObjectCondition<WebDriver> {
  private final String expectedUrl;

  public UrlStartingWith(String expectedUrl) {
    this.expectedUrl = expectedUrl;
  }

  @Nonnull
  @Override
  public String description() {
    return "should have url starting with " + expectedUrl;
  }

  @Nonnull
  @Override
  public String negativeDescription() {
    return "should not have url starting with " + expectedUrl;
  }

  @Override
  public boolean test(WebDriver webDriver) {
    return webDriver.getCurrentUrl().startsWith(expectedUrl);
  }

  @Nullable
  @Override
  public String actualValue(WebDriver webDriver) {
    return webDriver.getCurrentUrl();
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String describe(WebDriver webDriver) {
    return "webdriver";
  }
}
