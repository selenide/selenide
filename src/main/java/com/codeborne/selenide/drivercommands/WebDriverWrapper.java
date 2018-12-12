package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;

public class WebDriverWrapper implements Driver {
  private final WebDriver webDriver;
  private final Config config;

  public WebDriverWrapper(Config config, WebDriver webDriver) {
    this.config = config;
    this.webDriver = webDriver;
  }

  @Override
  public Config config() {
    return config;
  }

  @Override
  public Browser browser() {
    return new Browser(config.browser(), config.headless());
  }

  @Override
  public WebDriver getWebDriver() {
    return webDriver;
  }

  @Override
  public SelenideProxyServer getProxy() {
    return null;
  }

  @Override
  public WebDriver getAndCheckWebDriver() {
    return webDriver;
  }

  /**
   * Does not close webdriver.
   * This class holds a webdriver created by user - in this case user is responsible for closing webdriver by himself.
   */
  @Override
  public void close() {
  }
}
