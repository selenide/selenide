package com.codeborne.selenide;

import org.openqa.selenium.WebDriver;

record WebDriverConditional(Driver driver) implements Conditional<WebDriver> {
  @Override
  public WebDriver object() {
    return driver.getWebDriver();
  }
}
