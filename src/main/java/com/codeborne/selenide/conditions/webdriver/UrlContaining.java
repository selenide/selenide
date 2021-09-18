package com.codeborne.selenide.conditions.webdriver;

import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class UrlContaining extends UrlCondition {
  public UrlContaining(String expectedUrl) {
    super("containing ", expectedUrl);
  }

  @CheckReturnValue
  @Override
  public boolean test(WebDriver webDriver) {
    return webDriver.getCurrentUrl().contains(expectedUrl);
  }
}
