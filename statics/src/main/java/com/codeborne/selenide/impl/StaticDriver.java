package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;

/**
 * A `Driver` implementation which uses thread-local
 * webdriver and proxy from `WebDriverRunner`.
 *
 * @see WebDriverRunner
 * @see StaticConfig
 */
public class StaticDriver implements Driver {
  private final Config config = new StaticConfig();

  @Override
  public Config config() {
    return config;
  }

  @Override
  public Browser browser() {
    return new Browser(config.browser(), config.headless());
  }

  @Override
  public boolean hasWebDriverStarted() {
    return WebDriverRunner.hasWebDriverStarted();
  }

  @Override
  public WebDriver getWebDriver() {
    return WebDriverRunner.getWebDriver();
  }

  @Override
  public SelenideProxyServer getProxy() {
    return WebDriverRunner.getSelenideProxy();
  }

  @Override
  public WebDriver getAndCheckWebDriver() {
    return WebDriverRunner.getAndCheckWebDriver();
  }

  @Override
  public void close() {
    WebDriverRunner.closeWebDriver();
  }
}
