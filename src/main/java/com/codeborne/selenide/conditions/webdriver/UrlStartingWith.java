package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

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
    return "driver should have url starting with " + expectedUrl;
  }

  @Override
  public boolean test(WebDriver webDriver) {
    return webDriver.getCurrentUrl().startsWith(expectedUrl);
  }

  @Nullable
  @Override
  public Object actualValue(WebDriver webDriver) {
    return webDriver.getCurrentUrl();
  }
}
