package com.codeborne.selenide.conditions.webdriver;

import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class TitleContaining extends TitleCondition {

  public TitleContaining(String expectedTitle) {
    super("containing", expectedTitle);
  }

  @CheckReturnValue
  @Override
  public boolean test(WebDriver webDriver) {
    return webDriver.getTitle().contains(expectedTitle);
  }
}
