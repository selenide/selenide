package com.codeborne.selenide.conditions.webdriver;

import com.codeborne.selenide.ObjectCondition;
import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class TitleCondition implements ObjectCondition<WebDriver> {

  protected final String name;
  protected final String expectedTitle;

  public TitleCondition(String name, String expectedTitle) {
    this.name = name;
    this.expectedTitle = expectedTitle;
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

  @Nullable
  @Override
  public String actualValue(WebDriver webDriver) {
    return webDriver.getTitle();
  }

  @Nonnull
  @Override
  public String describe(WebDriver webDriver) {
    return "Page";
  }
}
