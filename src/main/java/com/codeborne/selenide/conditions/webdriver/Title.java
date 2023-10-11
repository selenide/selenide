package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Title implements ObjectCondition<WebDriver> {

  private final String expectedTitle;

  public Title(String expectedTitle) {
    this.expectedTitle = expectedTitle;
  }

  @Override
  @Nullable
  @CheckReturnValue
  public String expectedValue() {
    return expectedTitle;
  }

  @Nonnull
  @Override
  public String description() {
    return "should have title " + expectedTitle;
  }

  @Nonnull
  @Override
  public String negativeDescription() {
    return "should not have title " + expectedTitle;
  }

  @Nonnull
  @Override
  public String describe(WebDriver webDriver) {
    return "Page";
  }

  @CheckReturnValue
  @Override
  public CheckResult check(WebDriver webDriver) {
    String title = webDriver.getTitle();
    return result(webDriver, title.equals(expectedTitle), title);
  }

}
