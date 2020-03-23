package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class RemoteDriverFactory {
  public WebDriver create(Config config, Browser browser, MutableCapabilities capabilities) {
    try {
      setupCapabilities(config, browser, capabilities);
      RemoteWebDriver webDriver = new RemoteWebDriver(new URL(config.remote()), capabilities);
      webDriver.setFileDetector(new LocalFileDetector());
      return webDriver;
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + config.remote(), e);
    }
  }

  protected void setupCapabilities(Config config, Browser browser, MutableCapabilities capabilities) {
    capabilities.setCapability(CapabilityType.BROWSER_NAME, getBrowserNameForGrid(config, browser));
  }

  protected String getBrowserNameForGrid(Config config, Browser browser) {
    if (browser.isLegacyFirefox()) {
      return BrowserType.FIREFOX;
    }
    else if (browser.isIE()) {
      return BrowserType.IE;
    }
    else if (browser.isEdge()) {
      return BrowserType.EDGE;
    }
    else if (browser.isOpera()) {
      return BrowserType.OPERA;
    }
    else {
      return config.browser();
    }
  }
}
