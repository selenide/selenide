package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class CurrentFrameCondition implements ObjectCondition<WebDriver> {
  protected final String name;
  protected final String expectedUrl;

  protected CurrentFrameCondition(String name, String expectedUrl) {
    this.name = name;
    this.expectedUrl = expectedUrl;
  }

  @Nonnull
  @Override
  public String description() {
    return "should have url " + name + expectedUrl;
  }

  @Nonnull
  @Override
  public String negativeDescription() {
    return "should not have url " + name + expectedUrl;
  }

  @Nullable
  @Override
  public String actualValue(WebDriver webDriver) {
    return getCurrentFrameUrl(webDriver);
  }

  @CheckReturnValue
  @Nonnull
  protected String getCurrentFrameUrl(WebDriver webDriver) {
    return ((JavascriptExecutor) webDriver).executeScript("return window.location.href").toString();
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String describe(WebDriver webDriver) {
    return "current frame";
  }
}
