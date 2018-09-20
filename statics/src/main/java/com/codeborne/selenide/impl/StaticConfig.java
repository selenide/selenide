package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Configuration;
import org.openqa.selenium.remote.DesiredCapabilities;

class StaticConfig implements Config {
  @Override
  public String baseUrl() {
    return Configuration.baseUrl;
  }

  @Override
  public long timeout() {
    return Configuration.timeout;
  }

  @Override
  public long collectionsTimeout() {
    return Configuration.collectionsTimeout;
  }

  @Override
  public long pollingInterval() {
    return Configuration.pollingInterval;
  }

  @Override
  public long collectionsPollingInterval() {
    return Configuration.collectionsPollingInterval;
  }

  @Override
  public boolean holdBrowserOpen() {
    return Configuration.holdBrowserOpen;
  }

  @Override
  public boolean reopenBrowserOnFail() {
    return Configuration.reopenBrowserOnFail;
  }

  @Override
  public long closeBrowserTimeoutMs() {
    return Configuration.closeBrowserTimeoutMs;
  }

  @Override
  public boolean clickViaJs() {
    return Configuration.clickViaJs;
  }

  @Override
  public boolean captureJavascriptErrors() {
    return Configuration.captureJavascriptErrors;
  }

  @Override
  public boolean screenshots() {
    return Configuration.screenshots;
  }

  @Override
  public boolean savePageSource() {
    return Configuration.savePageSource;
  }

  @Override
  public String reportsFolder() {
    return Configuration.reportsFolder;
  }

  @Override
  public String reportsUrl() {
    return Configuration.reportsUrl;
  }

  @Override
  public boolean fastSetValue() {
    return Configuration.fastSetValue;
  }

  @Override
  public boolean versatileSetValue() {
    return Configuration.versatileSetValue;
  }

  @Override
  public boolean setValueChangeEvent() {
    return Configuration.setValueChangeEvent;
  }

  @Override
  public Configuration.SelectorMode selectorMode() {
    return Configuration.selectorMode;
  }

  @Override
  public Configuration.AssertionMode assertionMode() {
    return Configuration.assertionMode;
  }

  @Override
  public Configuration.FileDownloadMode fileDownload() {
    return Configuration.fileDownload;
  }

  @Override
  public boolean proxyEnabled() {
    return Configuration.proxyEnabled;
  }

  @Override
  public String proxyHost() {
    return Configuration.proxyHost;
  }

  @Override
  public int proxyPort() {
    return Configuration.proxyPort;
  }

  @Override
  public String browser() {
    return Configuration.browser;
  }

  @Override
  public boolean headless() {
    return Configuration.headless;
  }

  @Override
  public String remote() {
    return Configuration.remote;
  }

  @Override
  public String browserSize() {
    return Configuration.browserSize;
  }

  @Override
  public String browserVersion() {
    return Configuration.browserVersion;
  }

  @Override
  public String browserPosition() {
    return Configuration.browserPosition;
  }

  @Override
  public boolean startMaximized() {
    return Configuration.startMaximized;
  }

  @Override
  public boolean driverManagerEnabled() {
    return Configuration.driverManagerEnabled;
  }

  @Override
  public String browserBinary() {
    return Configuration.browserBinary;
  }

  @Override
  public String chromeSwitches() {
    return Configuration.chromeSwitches;
  }

  @Override
  public String pageLoadStrategy() {
    return Configuration.pageLoadStrategy;
  }

  @Override
  public DesiredCapabilities browserCapabilities() {
    return Configuration.browserCapabilities;
  }
}
