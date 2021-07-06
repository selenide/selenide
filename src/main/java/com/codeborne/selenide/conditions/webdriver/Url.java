package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Url implements ObjectCondition<WebDriver> {
  private final String expectedUrl;

  public Url(String expectedUrl) {
    this.expectedUrl = expectedUrl;
  }

  @Nonnull
  @Override
  public String description() {
    return "webdriver should have url " + expectedUrl;
  }

  @Nonnull
  @Override
  public String negativeDescription() {
    return "webdriver should not have url " + expectedUrl;
  }

  @Override
  public boolean test(WebDriver webDriver) {
    return webDriver.getCurrentUrl().equals(expectedUrl);
  }

  @Nullable
  @Override
  public Object actualValue(WebDriver webDriver) {
    return webDriver.getCurrentUrl();
  }
}
