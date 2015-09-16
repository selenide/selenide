package com.codeborne.selenide.impl;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

public interface WebDriverContainer {
  void addListener(WebDriverEventListener listener);
  WebDriver setWebDriver(WebDriver webDriver);
  WebDriver getWebDriver();
  void setProxy(Proxy webProxy);
  WebDriver getAndCheckWebDriver();
  void closeWebDriver();
  boolean hasWebDriverStarted();

  void clearBrowserCache();
  String getPageSource();
  String getCurrentUrl();
  String getCurrentFrameUrl();
}
