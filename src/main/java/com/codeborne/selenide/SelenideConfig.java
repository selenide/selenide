package com.codeborne.selenide;

import com.codeborne.selenide.Configuration.AssertionMode;
import com.codeborne.selenide.Configuration.SelectorMode;
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
  private SelectorMode selectorMode = Configuration.selectorMode;
  private AssertionMode assertionMode = Configuration.assertionMode;
  private Configuration.FileDownloadMode fileDownload = Configuration.fileDownload;
  private boolean proxyEnabled = Configuration.proxyEnabled;
  private String proxyHost = Configuration.proxyHost;
  private int proxyPort = Configuration.proxyPort;

  @Override
  public String baseUrl() {
    return baseUrl;
  }

  public SelenideConfig baseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return this;
  }

  @Override
  public BrowserConfig browser() {
    return browserConfig;
  }

  public SelenideConfig browser(BrowserConfig browserConfig) {
    this.browserConfig = browserConfig;
    return this;
  }

  @Override
  public long timeout() {
    return timeout;
  }

  public SelenideConfig timeout(long timeout) {
    this.timeout = timeout;
    return this;
  }

  @Override
  public long collectionsTimeout() {
    return collectionsTimeout;
  }

  public SelenideConfig collectionsTimeout(long collectionsTimeout) {
    this.collectionsTimeout = collectionsTimeout;
    return this;
  }

  @Override
  public long pollingInterval() {
    return pollingInterval;
  }

  public SelenideConfig pollingInterval(long pollingInterval) {
    this.pollingInterval = pollingInterval;
    return this;
  }

  @Override
  public long collectionsPollingInterval() {
    return collectionsPollingInterval;
  }

  public SelenideConfig collectionsPollingInterval(long collectionsPollingInterval) {
    this.collectionsPollingInterval = collectionsPollingInterval;
    return this;
  }

  @Override
  public boolean holdBrowserOpen() {
    return holdBrowserOpen;
  }

  public SelenideConfig holdBrowserOpen(boolean holdBrowserOpen) {
    this.holdBrowserOpen = holdBrowserOpen;
    return this;
  }

  @Override
  public boolean reopenBrowserOnFail() {
    return reopenBrowserOnFail;
  }

  public SelenideConfig reopenBrowserOnFail(boolean reopenBrowserOnFail) {
    this.reopenBrowserOnFail = reopenBrowserOnFail;
    return this;
  }

  @Override
  public long closeBrowserTimeoutMs() {
    return closeBrowserTimeoutMs;
  }

  public SelenideConfig closeBrowserTimeoutMs(long closeBrowserTimeoutMs) {
    this.closeBrowserTimeoutMs = closeBrowserTimeoutMs;
    return this;
  }

  @Override
  public boolean clickViaJs() {
    return clickViaJs;
  }

  public SelenideConfig clickViaJs(boolean clickViaJs) {
    this.clickViaJs = clickViaJs;
    return this;
  }

  @Override
  public boolean captureJavascriptErrors() {
    return captureJavascriptErrors;
  }

  public SelenideConfig captureJavascriptErrors(boolean captureJavascriptErrors) {
    this.captureJavascriptErrors = captureJavascriptErrors;
    return this;
  }

  @Override
  public boolean screenshots() {
    return screenshots;
  }

  public SelenideConfig screenshots(boolean screenshots) {
    this.screenshots = screenshots;
    return this;
  }

  @Override
  public boolean savePageSource() {
    return savePageSource;
  }

  public SelenideConfig savePageSource(boolean savePageSource) {
    this.savePageSource = savePageSource;
    return this;
  }

  @Override
  public String reportsFolder() {
    return reportsFolder;
  }

  public SelenideConfig reportsFolder(String reportsFolder) {
    this.reportsFolder = reportsFolder;
    return this;
  }

  @Override
  public String reportsUrl() {
    return reportsUrl;
  }

  public SelenideConfig reportsUrl(String reportsUrl) {
    this.reportsUrl = reportsUrl;
    return this;
  }

  @Override
  public boolean fastSetValue() {
    return fastSetValue;
  }

  public SelenideConfig fastSetValue(boolean fastSetValue) {
    this.fastSetValue = fastSetValue;
    return this;
  }

  @Override
  public boolean versatileSetValue() {
    return versatileSetValue;
  }

  public SelenideConfig versatileSetValue(boolean versatileSetValue) {
    this.versatileSetValue = versatileSetValue;
    return this;
  }

  @Override
  public boolean setValueChangeEvent() {
    return setValueChangeEvent;
  }

  public SelenideConfig setValueChangeEvent(boolean setValueChangeEvent) {
    this.setValueChangeEvent = setValueChangeEvent;
    return this;
  }

  @Override
  public SelectorMode selectorMode() {
    return selectorMode;
  }

  public SelenideConfig selectorMode(SelectorMode selectorMode) {
    this.selectorMode = selectorMode;
    return this;
  }

  @Override
  public AssertionMode assertionMode() {
    return assertionMode;
  }

  public SelenideConfig assertionMode(AssertionMode assertionMode) {
    this.assertionMode = assertionMode;
    return this;
  }

  @Override
  public Configuration.FileDownloadMode fileDownload() {
    return fileDownload;
  }

  public SelenideConfig fileDownload(Configuration.FileDownloadMode fileDownload) {
    this.fileDownload = fileDownload;
    return this;
  }

  @Override
  public boolean proxyEnabled() {
    return proxyEnabled;
  }

  public SelenideConfig proxyEnabled(boolean proxyEnabled) {
    this.proxyEnabled = proxyEnabled;
    return this;
  }

  @Override
  public String proxyHost() {
    return proxyHost;
  }

  public SelenideConfig proxyHost(String proxyHost) {
    this.proxyHost = proxyHost;
    return this;
  }

  @Override
  public int proxyPort() {
    return proxyPort;
  }

  public SelenideConfig proxyPort(int proxyPort) {
    this.proxyPort = proxyPort;
    return this;
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
