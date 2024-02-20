package com.codeborne.selenide;

import org.openqa.selenium.WebDriver;

import javax.annotation.Nonnull;

record WebDriverConditional(Driver driver) implements Conditional<WebDriver> {
  @Nonnull
  @Override
  public WebDriver object() {
    return driver.getWebDriver();
  }
}
