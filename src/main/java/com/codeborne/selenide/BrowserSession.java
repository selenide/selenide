package com.codeborne.selenide;

import org.openqa.selenium.WebDriver;

/**
 * Current browser session.
 * <p>
 * Allows checking conditions on webdriver (e.g. {@code webdriver().shouldHave(url(...))}).
 * </p>
 */
public final class BrowserSession implements Conditional<WebDriver> {
  private final Driver driver;

  BrowserSession(Driver driver) {
    this.driver = driver;
  }

  @Override
  public Driver driver() {
    return driver;
  }

  @Override
  public WebDriver object() {
    return driver.getWebDriver();
  }
}
