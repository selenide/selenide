package com.codeborne.selenide;

import org.openqa.selenium.remote.DesiredCapabilities;

public class SelenideConfig implements Config {
  private BrowserConfig browserConfig = new SelenideBrowserConfig();
  private long timeout;

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

    @Override
    public String pageLoadStrategy() {
      return pageLoadStrategy;
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
