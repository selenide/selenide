package com.codeborne.selenide;

import org.openqa.selenium.remote.DesiredCapabilities;

public class SelenideConfig implements Config {
  private BrowserConfig browserConfig = new SelenideBrowserConfig();
  private String baseUrl = Configuration.baseUrl;
  private long timeout = Configuration.timeout;
  private long collectionsTimeout = Configuration.collectionsTimeout;
  private long pollingInterval = Configuration.pollingInterval;
  private long collectionsPollingInterval = Configuration.collectionsPollingInterval;
  private boolean holdBrowserOpen = Configuration.holdBrowserOpen;
  private boolean reopenBrowserOnFail = Configuration.reopenBrowserOnFail;
  private long closeBrowserTimeoutMs = Configuration.closeBrowserTimeoutMs;
  private boolean clickViaJs = Configuration.clickViaJs;
  private boolean captureJavascriptErrors = Configuration.captureJavascriptErrors;
  private boolean screenshots = Configuration.screenshots;
  private boolean savePageSource = Configuration.savePageSource;
  private String reportsFolder = Configuration.reportsFolder;
  private String reportsUrl = Configuration.reportsUrl;
  private boolean fastSetValue = Configuration.fastSetValue;
  private boolean versatileSetValue = Configuration.versatileSetValue;
  private boolean setValueChangeEvent = Configuration.setValueChangeEvent;
  private Configuration.SelectorMode selectorMode = Configuration.selectorMode;
  private Configuration.AssertionMode assertionMode = Configuration.assertionMode;
  private Configuration.FileDownloadMode fileDownload = Configuration.fileDownload;
  private boolean proxyEnabled = Configuration.proxyEnabled;
  private String proxyHost = Configuration.proxyHost;
  private int proxyPort = Configuration.proxyPort;

  @Override
  public String baseUrl() {
    return baseUrl;
  }

  @Override
  public BrowserConfig browser() {
    return browserConfig;
  }

  public void browser(BrowserConfig browserConfig) {
    this.browserConfig = browserConfig;
  }

  @Override
  public long timeout() {
    return timeout;
  }

  @Override
  public long collectionsTimeout() {
    return collectionsTimeout;
  }

  @Override
  public long pollingInterval() {
    return pollingInterval;
  }

  @Override
  public long collectionsPollingInterval() {
    return collectionsPollingInterval;
  }

  @Override
  public boolean holdBrowserOpen() {
    return holdBrowserOpen;
  }

  @Override
  public boolean reopenBrowserOnFail() {
    return reopenBrowserOnFail;
  }

  @Override
  public long closeBrowserTimeoutMs() {
    return closeBrowserTimeoutMs;
  }

  @Override
  public boolean clickViaJs() {
    return clickViaJs;
  }

  @Override
  public boolean captureJavascriptErrors() {
    return captureJavascriptErrors;
  }

  @Override
  public boolean screenshots() {
    return screenshots;
  }

  @Override
  public boolean savePageSource() {
    return savePageSource;
  }

  @Override
  public String reportsFolder() {
    return reportsFolder;
  }

  @Override
  public String reportsUrl() {
    return reportsUrl;
  }

  @Override
  public boolean fastSetValue() {
    return fastSetValue;
  }

  @Override
  public boolean versatileSetValue() {
    return versatileSetValue;
  }

  @Override
  public boolean setValueChangeEvent() {
    return setValueChangeEvent;
  }

  @Override
  public Configuration.SelectorMode selectorMode() {
    return selectorMode;
  }

  @Override
  public Configuration.AssertionMode assertionMode() {
    return assertionMode;
  }

  @Override
  public Configuration.FileDownloadMode fileDownload() {
    return fileDownload;
  }

  @Override
  public boolean proxyEnabled() {
    return proxyEnabled;
  }

  @Override
  public String proxyHost() {
    return proxyHost;
  }

  @Override
  public int proxyPort() {
    return proxyPort;
  }

  public static class SelenideBrowserConfig implements BrowserConfig {
    private String browser;
    private boolean headless;
    private String remote;
    private String browserSize;
    private String browserVersion;
    private String browserPosition;
    private boolean startMaximized = false;
    private boolean driverManagerEnabled = true;
    private String browserBinary = "";
    private String chromeSwitches;
    private String pageLoadStrategy = "normal";
    private DesiredCapabilities browserCapabilities;

    @Override
    public String browser() {
      return browser;
    }

    public SelenideBrowserConfig browser(String browser) {
      this.browser = browser;
      return this;
    }

    @Override
    public boolean headless() {
      return headless;
    }

    public SelenideBrowserConfig headless(boolean headless) {
      this.headless = headless;
      return this;
    }

    @Override
    public String remote() {
      return remote;
    }

    public SelenideBrowserConfig remote(String remote) {
      this.remote = remote;
      return this;
    }

    @Override
    public String browserSize() {
      return browserSize;
    }

    public SelenideBrowserConfig browserSize(String browserSize) {
      this.browserSize = browserSize;
      return this;
    }

    @Override
    public String browserVersion() {
      return browserVersion;
    }

    public SelenideBrowserConfig browserVersion(String browserVersion) {
      this.browserVersion = browserVersion;
      return this;
    }

    @Override
    public String browserPosition() {
      return browserPosition;
    }

    public SelenideBrowserConfig browserPosition(String browserPosition) {
      this.browserPosition = browserPosition;
      return this;
    }

    @Override
    public boolean startMaximized() {
      return startMaximized;
    }

    public SelenideBrowserConfig startMaximized(boolean startMaximized) {
      this.startMaximized = startMaximized;
      return this;
    }

    @Override
    public boolean driverManagerEnabled() {
      return driverManagerEnabled;
    }

    public SelenideBrowserConfig driverManagerEnabled(boolean driverManagerEnabled) {
      this.driverManagerEnabled = driverManagerEnabled;
      return this;
    }

    @Override
    public String browserBinary() {
      return browserBinary;
    }

    public SelenideBrowserConfig browserBinary(String browserBinary) {
      this.browserBinary = browserBinary;
      return this;
    }

    @Override
    public String chromeSwitches() {
      return chromeSwitches;
    }

    public SelenideBrowserConfig chromeSwitches(String chromeSwitches) {
      this.chromeSwitches = chromeSwitches;
      return this;
    }

    @Override
    public String pageLoadStrategy() {
      return pageLoadStrategy;
    }

    public SelenideBrowserConfig pageLoadStrategy(String pageLoadStrategy) {
      this.pageLoadStrategy = pageLoadStrategy;
      return this;
    }

    @Override
    public DesiredCapabilities browserCapabilities() {
      return browserCapabilities;
    }

    public SelenideBrowserConfig browserCapabilities(DesiredCapabilities browserCapabilities) {
      this.browserCapabilities = browserCapabilities;
      return this;
    }
  }
}
