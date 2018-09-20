package com.codeborne.selenide;

import org.openqa.selenium.remote.DesiredCapabilities;

public interface Config {
  String browser();
  boolean headless();
  String remote();
  String browserSize();
  String browserVersion();
  String browserPosition();
  boolean startMaximized();
  boolean driverManagerEnabled();
  String browserBinary();
  String chromeSwitches();
  String pageLoadStrategy();
  DesiredCapabilities browserCapabilities();

  String baseUrl();
  long timeout();
  long collectionsTimeout();
  long pollingInterval();
  long collectionsPollingInterval();
  boolean holdBrowserOpen();
  boolean reopenBrowserOnFail();
  long closeBrowserTimeoutMs();
  boolean clickViaJs();
  boolean captureJavascriptErrors();
  boolean screenshots();
  boolean savePageSource();
  String reportsFolder();
  String reportsUrl();
  boolean fastSetValue();
  boolean versatileSetValue();
  boolean setValueChangeEvent();
  Configuration.SelectorMode selectorMode();
  Configuration.AssertionMode assertionMode();
  Configuration.FileDownloadMode fileDownload();
  boolean proxyEnabled();
  String proxyHost();
  int proxyPort();
}
