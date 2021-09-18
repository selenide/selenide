package com.codeborne.selenide.conditions.webdriver;

import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CurrentFrameUrlContaining extends CurrentFrameCondition {
  public CurrentFrameUrlContaining(String expectedUrl) {
    super("containing ", expectedUrl);
  }

  @CheckReturnValue
  @Override
  public boolean test(WebDriver webDriver) {
    return getCurrentFrameUrl(webDriver).contains(expectedUrl);
  }
}
