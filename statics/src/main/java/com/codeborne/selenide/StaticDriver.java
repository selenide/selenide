package com.codeborne.selenide;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * A `Driver` implementation which uses thread-local
 * webdriver and proxy from `WebDriverRunner`.
 */
@ParametersAreNonnullByDefault
class StaticDriver implements Driver {
  private final Config config = new ThreadLocalSelenideConfig();

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
  @CheckReturnValue
  @Nonnull
  public WebDriver getAndCheckWebDriver() {
    return WebDriverRunner.getAndCheckWebDriver();
  }

  @Override
  @CheckReturnValue
  @Nullable
  public DownloadsFolder browserDownloadsFolder() {
    return WebDriverRunner.getBrowserDownloadsFolder();
  }

  @Override
  public void close() {
    WebDriverRunner.closeWebDriver();
  }
}
