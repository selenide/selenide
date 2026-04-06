package com.codeborne.selenide.mcp;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import org.jspecify.annotations.Nullable;

class BrowserSession {
  private final SelenideConfig config;
  @Nullable
  private SelenideDriver driver;

  BrowserSession(SelenideConfig config) {
    this.config = config;
  }

  SelenideDriver getDriver() {
    if (driver == null || !driver.hasWebDriverStarted()) {
      driver = new SelenideDriver(config);
    }
    return driver;
  }

  boolean isStarted() {
    return driver != null && driver.hasWebDriverStarted();
  }

  void close() {
    if (driver != null) {
      driver.close();
      driver = null;
    }
  }
}
