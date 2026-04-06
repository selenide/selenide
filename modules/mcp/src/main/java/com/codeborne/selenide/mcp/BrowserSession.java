package com.codeborne.selenide.mcp;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import org.jspecify.annotations.Nullable;

public class BrowserSession {
  private final SelenideConfig config;
  @Nullable
  private SelenideDriver driver;

  public BrowserSession(SelenideConfig config) {
    this.config = config;
  }

  public SelenideDriver getDriver() {
    if (driver == null || !driver.hasWebDriverStarted()) {
      driver = new SelenideDriver(config);
    }
    return driver;
  }

  public boolean isStarted() {
    return driver != null && driver.hasWebDriverStarted();
  }

  public void close() {
    if (driver != null) {
      driver.close();
      driver = null;
    }
  }
}
