package com.codeborne.selenide;

import org.openqa.selenium.remote.DesiredCapabilities;

public interface Config {
  interface BrowserConfig {
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
  }

  BrowserConfig browser();
  long timeout();
}
