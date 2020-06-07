package com.codeborne.selenide.impl;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface WebDriverContainer {
  void addListener(WebDriverEventListener listener);
  void setWebDriver(WebDriver webDriver);
  void setWebDriver(WebDriver webDriver, SelenideProxyServer selenideProxy);
  void resetWebDriver();

  @CheckReturnValue
  @Nonnull
  WebDriver getWebDriver();

  @CheckReturnValue
  @Nullable
  SelenideProxyServer getProxyServer();

  void setProxy(Proxy webProxy);

  @CheckReturnValue
  @Nonnull
  WebDriver getAndCheckWebDriver();

  void closeWindow();
  void closeWebDriver();
  boolean hasWebDriverStarted();

  void clearBrowserCache();

  @CheckReturnValue
  @Nonnull
  String getPageSource();

  @CheckReturnValue
  @Nonnull
  String getCurrentUrl();

  @CheckReturnValue
  @Nonnull
  String getCurrentFrameUrl();
}
