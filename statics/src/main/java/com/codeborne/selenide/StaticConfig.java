package com.codeborne.selenide;

import org.jspecify.annotations.Nullable;
import org.openqa.selenium.MutableCapabilities;

/**
 * A non-static facade for static fields in {@link com.codeborne.selenide.Configuration}
 *
 * It was created only to keep backward compatibility in Selenide 5.0.0: every time when somebody modifies, say,
 * {@link com.codeborne.selenide.Configuration#timeout}, it will immediately reflect in {@link StaticConfig#timeout()}
 *
 * This class should not be normally used in end user's code.
 */
final class StaticConfig implements Config {
  @Override
  public String baseUrl() {
    return Configuration.baseUrl;
  }

  @Override
  public long timeout() {
    return Configuration.timeout;
  }

  @Override
  public long pollingInterval() {
    return Configuration.pollingInterval;
  }

  @Deprecated
  @Override
  public boolean holdBrowserOpen() {
    return Configuration.holdBrowserOpen;
  }

  @Override
  public boolean reopenBrowserOnFail() {
    return Configuration.reopenBrowserOnFail;
  }

  @Override
  public boolean clickViaJs() {
    return Configuration.clickViaJs;
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
  public String downloadsFolder() {
    return Configuration.downloadsFolder;
  }

  @Nullable
  @Override
  public String reportsUrl() {
    return Configuration.reportsUrl;
  }

  @Override
  public boolean fastSetValue() {
    return Configuration.fastSetValue;
  }

  @Override
  public TextCheck textCheck() {
    return Configuration.textCheck;
  }

  @Override
  public SelectorMode selectorMode() {
    return Configuration.selectorMode;
  }

  @Override
  public AssertionMode assertionMode() {
    return Configuration.assertionMode;
  }

  @Override
  public FileDownloadMode fileDownload() {
    return Configuration.fileDownload;
  }

  @Override
  public boolean proxyEnabled() {
    return Configuration.proxyEnabled;
  }

  @Nullable
  @Override
  public String proxyHost() {
    return Configuration.proxyHost;
  }

  @Override
  public int proxyPort() {
    return Configuration.proxyPort;
  }

  @Override
  public long remoteReadTimeout() {
    return Configuration.remoteReadTimeout;
  }

  @Override
  public long remoteConnectionTimeout() {
    return Configuration.remoteConnectionTimeout;
  }

  @Override
  public String browser() {
    return Configuration.browser;
  }

  @Override
  public boolean headless() {
    return Configuration.headless;
  }

  @Nullable
  @Override
  public String remote() {
    return Configuration.remote;
  }

  @Nullable
  @Override
  public String browserSize() {
    return Configuration.browserSize;
  }

  @Nullable
  @Override
  public String browserVersion() {
    return Configuration.browserVersion;
  }

  @Nullable
  @Override
  public String browserPosition() {
    return Configuration.browserPosition;
  }

  @Override
  public boolean webdriverLogsEnabled() {
    return Configuration.webdriverLogsEnabled;
  }

  @Nullable
  @Override
  public String browserBinary() {
    return Configuration.browserBinary;
  }

  @Override
  public String pageLoadStrategy() {
    return Configuration.pageLoadStrategy;
  }

  @Override
  public long pageLoadTimeout() {
    return Configuration.pageLoadTimeout;
  }

  @Override
  public MutableCapabilities browserCapabilities() {
    return Configuration.browserCapabilities;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }
}
