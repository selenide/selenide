package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.drivercommands.CloseDriverCommand;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;

import static java.util.Objects.requireNonNull;

/**
 * An instance of webdriver with its resources: config, proxy and downloads folder.
 */
public record WebDriverInstance(
  long threadId,
  Config config,
  WebDriver webDriver,
  @Nullable SelenideProxyServer proxy,
  @Nullable DownloadsFolder downloadsFolder) implements Disposable {

  private static final CloseDriverCommand closeDriverCommand = new CloseDriverCommand();

  public WebDriverInstance(
    Config config,
    WebDriver webDriver,
    @Nullable SelenideProxyServer proxy,
    @Nullable DownloadsFolder downloadsFolder) {
    this(Thread.currentThread().getId(), config, webDriver, proxy, downloadsFolder);
  }

  public WebDriverInstance {
    requireNonNull(config, "config must not be null");
    requireNonNull(webDriver, "webDriver must not be null");
  }

  @Override
  public SelenideProxyServer proxy() {
    if (!config.proxyEnabled()) {
      throw new IllegalStateException("Proxy server is not enabled. You need to set proxyEnabled=true before opening a browser.");
    }
    if (proxy == null) {
      throw new IllegalStateException("config.proxyEnabled == true but proxy server is not created.");
    }
    if (!proxy.isStarted()) {
      throw new IllegalStateException("config.proxyEnabled == true but proxy server is not started.");
    }
    return proxy;
  }

  @Override
  public void dispose() {
    closeDriverCommand.close(this);
  }
}
