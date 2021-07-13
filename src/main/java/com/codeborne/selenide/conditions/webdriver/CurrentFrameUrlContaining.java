package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CurrentFrameUrlContaining implements ObjectCondition<WebDriver> {
  private final String expectedUrl;

  public CurrentFrameUrlContaining(String expectedUrl) {
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
    return getCurrentFrameUrl(webDriver).contains(expectedUrl);
  }

  @Nullable
  @Override
  public Object actualValue(WebDriver webDriver) {
    return getCurrentFrameUrl(webDriver);
  }

  @CheckReturnValue
  @Nonnull
  private static String getCurrentFrameUrl(WebDriver webDriver) {
    return ((JavascriptExecutor) webDriver).executeScript("return window.location.href").toString();
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String describe(WebDriver webDriver) {
    return "current frame";
  }
}
