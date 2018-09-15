package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.Configuration;
import org.openqa.selenium.remote.DesiredCapabilities;

class StaticConfig implements Config {
  @Override
  public BrowserConfig browser() {
    return new StaticBrowserConfig();
  }

  @Override
  public long timeout() {
    return Configuration.timeout;
  }

  private class StaticBrowserConfig implements BrowserConfig {
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
}
