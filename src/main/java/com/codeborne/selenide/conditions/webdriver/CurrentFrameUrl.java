package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CurrentFrameUrl implements ObjectCondition<WebDriver> {
  private final String expectedUrl;

  public CurrentFrameUrl(String expectedUrl) {
    this.expectedUrl = expectedUrl;
  }

  @Nonnull
  @Override
  public String description() {
    return "current frame should have url " + expectedUrl;
  }

  @Nonnull
  @Override
  public String negativeDescription() {
    return "current frame should not have url " + expectedUrl;
  }

  @Override
  public boolean test(WebDriver webDriver) {
    return getCurrentFrameUrl(webDriver).equals(expectedUrl);
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
}
