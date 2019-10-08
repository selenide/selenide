package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;
import org.testng.internal.Nullable;

import javax.annotation.Nonnull;

import static java.util.Objects.requireNonNull;

/**
 * A `Driver` implementation which uses given webdriver [and proxy].
 * It doesn't open a new browser.
 * It doesn't start a new proxy.
 */
public class WebDriverWrapper implements Driver {
  private final Config config;
  private final WebDriver webDriver;
  private final SelenideProxyServer selenideProxy;

  public WebDriverWrapper(@Nonnull Config config, @Nonnull WebDriver webDriver, @Nullable SelenideProxyServer selenideProxy) {
    requireNonNull(config, "config must not be null");
    requireNonNull(webDriver, "webDriver must not be null");

    this.config = config;
    this.webDriver = webDriver;
    this.selenideProxy = selenideProxy;
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
  public boolean hasWebDriverStarted() {
    return webDriver != null;
  }

  @Override
  public WebDriver getWebDriver() {
    return webDriver;
  }

  @Override
  public SelenideProxyServer getProxy() {
    return selenideProxy;
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
