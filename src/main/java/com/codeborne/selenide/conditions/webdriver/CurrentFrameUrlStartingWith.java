package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CurrentFrameUrlStartingWith implements ObjectCondition<WebDriver> {
  private final String expectedUrl;

  public CurrentFrameUrlStartingWith(String expectedUrl) {
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
    return getCurrentFrameUrl(webDriver).startsWith(expectedUrl);
  }

  @Nullable
  @Override
  public String actualValue(WebDriver webDriver) {
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
