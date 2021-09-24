package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class Title implements ObjectCondition<WebDriver> {

  private final String title;

  public Title(String title) {
    this.title = title;
  }

  @Nullable
  @Override
  public String actualValue(WebDriver webDriver) {
    return webDriver.getTitle();
  }

  @Override
  @Nullable
  @CheckReturnValue
  public String expectedValue() {
    return title;
  }

  @Nonnull
  @Override
  public String description() {
    return "should have title " + title;
  }

  @Nonnull
  @Override
  public String negativeDescription() {
    return "should not have title " + title;
  }

  @Nonnull
  @Override
  public String describe(WebDriver webDriver) {
    return "Page";
  }

  @CheckReturnValue
  @Override
  public boolean test(WebDriver webDriver) {
    return webDriver.getTitle().equals(title);
  }

}
