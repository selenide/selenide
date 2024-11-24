package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverListener;

public interface WebDriverContainer {
  void addListener(WebDriverListener listener);
  void removeListener(WebDriverListener listener);
  void setWebDriver(WebDriver webDriver);
  void setWebDriver(WebDriver webDriver, @Nullable SelenideProxyServer selenideProxy);
  void setWebDriver(WebDriver webDriver, @Nullable SelenideProxyServer selenideProxy, DownloadsFolder browserDownloadsFolder);

  WebDriver getWebDriver();

  SelenideProxyServer getProxyServer();

  void setProxy(@Nullable Proxy webProxy);

  WebDriver getAndCheckWebDriver();

  @Nullable
  DownloadsFolder getBrowserDownloadsFolder();

  void closeWindow();
  void closeWebDriver();
  boolean hasWebDriverStarted();

  void using(WebDriver driver, @Nullable SelenideProxyServer proxy, @Nullable DownloadsFolder downloadsFolder, Runnable lambda);
  void inNewBrowser(Runnable lambda);
  void inNewBrowser(Config config, Runnable lambda);

  void clearBrowserCache();

  @Nullable
  String getPageSource();

  @Nullable
  String getCurrentUrl();

  String getCurrentFrameUrl();
}
