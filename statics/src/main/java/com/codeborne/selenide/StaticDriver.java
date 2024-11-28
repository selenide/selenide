package com.codeborne.selenide;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;

/**
 * A `Driver` implementation which uses thread-local
 * webdriver and proxy from `WebDriverRunner`.
 */
class StaticDriver implements Driver {
  private final Config config = new ThreadLocalSelenideConfig();

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
  @Nullable
  public DownloadsFolder browserDownloadsFolder() {
    return WebDriverRunner.getBrowserDownloadsFolder();
  }

  @Override
  public void close() {
    WebDriverRunner.closeWebDriver();
  }
}
