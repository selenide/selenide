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
  String pageLoadStrategy();
  DesiredCapabilities browserCapabilities();

  String baseUrl();
  long timeout();
  long pollingInterval();
  boolean holdBrowserOpen();
  boolean reopenBrowserOnFail();
  boolean clickViaJs();
  boolean screenshots();
  boolean savePageSource();
  String reportsFolder();
  String reportsUrl();
  boolean fastSetValue();
  boolean versatileSetValue();
  SelectorMode selectorMode();
  AssertionMode assertionMode();
  FileDownloadMode fileDownload();
  boolean proxyEnabled();
  String proxyHost();
  int proxyPort();

}
