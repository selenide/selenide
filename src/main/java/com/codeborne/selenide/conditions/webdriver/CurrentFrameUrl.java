package com.codeborne.selenide.conditions.webdriver;

import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CurrentFrameUrl extends CurrentFrameCondition {
  public CurrentFrameUrl(String expectedUrl) {
    super("", expectedUrl);
  }

  @CheckReturnValue
  @Override
  public boolean test(WebDriver webDriver) {
    return getCurrentFrameUrl(webDriver).equals(expectedUrl);
  }
}
