package com.codeborne.selenide;

import org.openqa.selenium.MutableCapabilities;

import static java.lang.ThreadLocal.withInitial;

final class ThreadLocalSelenideConfig implements Config {
  private static final StaticConfig staticConfig = new StaticConfig();
  private static final ThreadLocal<Config> config = withInitial(() -> staticConfig);

  void set(Config perThreadConfig) {
    ThreadLocalSelenideConfig.config.set(perThreadConfig);
  }

  Config get() {
    return ThreadLocalSelenideConfig.config.get();
  }

  void reset() {
    ThreadLocalSelenideConfig.config.remove();
  }

  @Override
  public String browser() {
    return config.get().browser();
  }

  @Override
  public boolean headless() {
    return config.get().headless();
  }

  @Override
  public String remote() {
    return config.get().remote();
  }

  @Override
  public String browserSize() {
    return config.get().browserSize();
  }

  @Override
  public String browserVersion() {
    return config.get().browserVersion();
  }

  @Override
  public String browserPosition() {
    return config.get().browserPosition();
  }

  @Override
  public boolean webdriverLogsEnabled() {
    return config.get().webdriverLogsEnabled();
  }

  @Override
  public String browserBinary() {
    return config.get().browserBinary();
  }

  @Override
  public String pageLoadStrategy() {
    return config.get().pageLoadStrategy();
  }

  @Override
  public long pageLoadTimeout() {
    return config.get().pageLoadTimeout();
  }

  @Override
  public MutableCapabilities browserCapabilities() {
    return config.get().browserCapabilities();
  }

  @Override
  public String baseUrl() {
    return config.get().baseUrl();
  }

  @Override
  public long timeout() {
    return config.get().timeout();
  }

  @Override
  public long pollingInterval() {
    return config.get().pollingInterval();
  }

  @Override
  public boolean holdBrowserOpen() {
    return config.get().holdBrowserOpen();
  }

  @Override
  public boolean reopenBrowserOnFail() {
    return config.get().reopenBrowserOnFail();
  }

  @Override
  public boolean clickViaJs() {
    return config.get().clickViaJs();
  }

  @Override
  public boolean screenshots() {
    return config.get().screenshots();
  }

  @Override
  public boolean savePageSource() {
    return config.get().savePageSource();
  }

  @Override
  public String reportsFolder() {
    return config.get().reportsFolder();
  }

  @Override
  public String downloadsFolder() {
    return config.get().downloadsFolder();
  }

  @Override
  public String reportsUrl() {
    return config.get().reportsUrl();
  }

  @Override
  public boolean fastSetValue() {
    return config.get().fastSetValue();
  }

  @Override
  public TextCheck textCheck() {
    return config.get().textCheck();
  }

  @Override
  public SelectorMode selectorMode() {
    return config.get().selectorMode();
  }

  @Override
  public AssertionMode assertionMode() {
    return config.get().assertionMode();
  }

  @Override
  public FileDownloadMode fileDownload() {
    return config.get().fileDownload();
  }

  @Override
  public boolean proxyEnabled() {
    return config.get().proxyEnabled();
  }

  @Override
  public String proxyHost() {
    return config.get().proxyHost();
  }

  @Override
  public int proxyPort() {
    return config.get().proxyPort();
  }

  @Override
  public long remoteReadTimeout() {
    return config.get().remoteReadTimeout();
  }

  @Override
  public long remoteConnectionTimeout() {
    return config.get().remoteConnectionTimeout();
  }
}
