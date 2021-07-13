package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UrlContaining implements ObjectCondition<WebDriver> {
  private final String expectedUrl;

  public UrlContaining(String expectedUrl) {
    this.expectedUrl = expectedUrl;
  }

  @Nonnull
  @Override
  public String description() {
    return "should have url containing " + expectedUrl;
  }

  @Nonnull
  @Override
  public String negativeDescription() {
    return "should not have url containing " + expectedUrl;
  }

  @Override
  public boolean test(WebDriver webDriver) {
    return webDriver.getCurrentUrl().contains(expectedUrl);
  }

  @Nullable
  @Override
  public Object actualValue(WebDriver webDriver) {
    return webDriver.getCurrentUrl();
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String describe(WebDriver webDriver) {
    return "webdriver";
  }
}
