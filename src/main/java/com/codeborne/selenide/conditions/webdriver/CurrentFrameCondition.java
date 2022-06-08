package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.impl.JavaScript.jsExecutor;

@ParametersAreNonnullByDefault
public abstract class CurrentFrameCondition implements ObjectCondition<WebDriver> {
  private final String name;
  protected final String expectedUrl;

  protected CurrentFrameCondition(String name, String expectedUrl) {
    this.name = name;
    this.expectedUrl = expectedUrl;
  }

  @CheckReturnValue
  @Nonnull
  @Override
  public String description() {
    return "should have url " + name + expectedUrl;
  }

  @CheckReturnValue
  @Nonnull
  @Override
  public String negativeDescription() {
    return "should not have url " + name + expectedUrl;
  }

  @CheckReturnValue
  @Nullable
  @Override
  public String actualValue(WebDriver webDriver) {
    return getCurrentFrameUrl(webDriver);
  }

  @Override
  @Nullable
  @CheckReturnValue
  public String expectedValue() {
    return expectedUrl;
  }

  @CheckReturnValue
  @Nonnull
  protected String getCurrentFrameUrl(WebDriver webDriver) {
    return jsExecutor(webDriver).executeScript("return window.location.href").toString();
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String describe(WebDriver webDriver) {
    return "current frame";
  }
}
