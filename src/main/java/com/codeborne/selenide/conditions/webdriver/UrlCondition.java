package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class UrlCondition implements ObjectCondition<WebDriver> {
  private final String name;
  protected final String expectedUrl;

  protected UrlCondition(String name, String expectedUrl) {
    this.name = name;
    this.expectedUrl = expectedUrl;
  }

  @Nullable
  @CheckReturnValue
  @Override
  public String actualValue(WebDriver webDriver) {
    return webDriver.getCurrentUrl();
  }

  @Override
  @Nullable
  @CheckReturnValue
  public String expectedValue() {
    return expectedUrl;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String description() {
    return "should have url " + name + expectedUrl;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String negativeDescription() {
    return "should not have url " + name + expectedUrl;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String describe(WebDriver webDriver) {
    return "webdriver";
  }
}
