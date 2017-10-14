package com.codeborne.selenide.webdriver;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.remote;
import static com.codeborne.selenide.WebDriverRunner.*;

class RemoteDriverFactory extends AbstractDriverFactory {

  @Override
  boolean supports() {
    return remote != null;
  }

  @Override
  WebDriver create(Proxy proxy) {
    return createRemoteDriver(remote, browser, proxy);
  }

  private WebDriver createRemoteDriver(final String remote, final String browser, final Proxy proxy) {
    String browserName;
    if (isLegacyFirefox()) {
      browserName = BrowserType.FIREFOX;
    } else if (isIE()) {
      browserName = BrowserType.IE;
    } else if (isEdge()) {
      browserName = BrowserType.EDGE;
    } else if (isOpera()) {
      browserName = BrowserType.OPERA_BLINK;
    } else {
      browserName = browser;
    }

    try {
      DesiredCapabilities capabilities = createCommonCapabilities(proxy);
      capabilities.setBrowserName(browserName);
      RemoteWebDriver webDriver = new RemoteWebDriver(new URL(remote), capabilities);
      webDriver.setFileDetector(new LocalFileDetector());
      return webDriver;
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + remote, e);
    }
  }
}
