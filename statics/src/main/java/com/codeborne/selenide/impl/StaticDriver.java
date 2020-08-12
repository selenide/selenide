package com.codeborne.selenide.impl;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A `Driver` implementation which uses thread-local
 * webdriver and proxy from `WebDriverRunner`.
 *
 * @see WebDriverRunner
 * @see StaticConfig
 */
@ParametersAreNonnullByDefault
public class StaticDriver implements Driver {
  private final Config config = new StaticConfig();

  @Override
  @CheckReturnValue
  @Nonnull
  public Config config() {
    return config;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Browser browser() {
    return new Browser(config.browser(), config.headless());
  }

  @Override
  @CheckReturnValue
  public boolean hasWebDriverStarted() {
    return WebDriverRunner.hasWebDriverStarted();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver getWebDriver() {
    return WebDriverRunner.getWebDriver();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public SelenideProxyServer getProxy() {
    return WebDriverRunner.getSelenideProxy();
  }

  @Override
  public WebDriver getAndCheckWebDriver() {
    return WebDriverRunner.getAndCheckWebDriver();
  }

  @Override
  public DownloadsFolder browserDownloadsFolder() {
    return WebDriverRunner.getBrowserDownloadsFolder();
  }

  @Override
  public void close() {
    WebDriverRunner.closeWebDriver();
  }
}
