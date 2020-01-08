package com.codeborne.selenide.impl;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

public interface WebDriverContainer {
  void addListener(WebDriverEventListener listener);
  void setWebDriver(WebDriver webDriver);
  void setWebDriver(WebDriver webDriver, SelenideProxyServer selenideProxy);
  void resetWebDriver();
  WebDriver getWebDriver();
  SelenideProxyServer getProxyServer();
  void setProxy(Proxy webProxy);
  WebDriver getAndCheckWebDriver();
  void closeWindow();
  void closeWebDriver();
  boolean hasWebDriverStarted();

  void clearBrowserCache();
  String getPageSource();
  String getCurrentUrl();
  String getCurrentFrameUrl();
}
