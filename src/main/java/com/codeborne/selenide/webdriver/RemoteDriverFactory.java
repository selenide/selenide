package com.codeborne.selenide.webdriver;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.browserBinary;
import static com.codeborne.selenide.Configuration.headless;
import static com.codeborne.selenide.Configuration.remote;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isEdge;
import static com.codeborne.selenide.WebDriverRunner.isFirefox;
import static com.codeborne.selenide.WebDriverRunner.isIE;
import static com.codeborne.selenide.WebDriverRunner.isLegacyFirefox;
import static com.codeborne.selenide.WebDriverRunner.isOpera;

class RemoteDriverFactory extends AbstractDriverFactory {


  private static final Logger log = Logger.getLogger(RemoteDriverFactory.class.getName());

  @Override
  boolean supports() {
    return remote != null;
  }

  @Override
  WebDriver create(Proxy proxy) {
    return createRemoteDriver(remote, browser, proxy);
  }

  private WebDriver createRemoteDriver(final String remote, final String browser, final Proxy proxy) {
    try {
      DesiredCapabilities capabilities = getDriverCapabilities(proxy);
      RemoteWebDriver webDriver = new RemoteWebDriver(new URL(remote), capabilities);
      webDriver.setFileDetector(new LocalFileDetector());
      return webDriver;
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + remote, e);
    }
  }

  DesiredCapabilities getDriverCapabilities(Proxy proxy) {
    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    capabilities.setBrowserName(getBrowserNameForGrid());
    if (headless) {
      capabilities.merge(getHeadlessCapabilities());
    }
    if (!browserBinary.isEmpty()) {
      capabilities.merge(getBrowserBinaryCapabilites());
    }
    return capabilities;
  }

  Capabilities getBrowserBinaryCapabilites() {
    log.info("Using browser binary: " + browserBinary);
    if (isChrome()) {
      ChromeOptions options = new ChromeOptions();
      options.setBinary(browserBinary);
      return options;
    } else if (isFirefox()) {
      FirefoxOptions options = new FirefoxOptions();
      options.setBinary(browserBinary);
      return options;
    } else {
      log.warning("Changing browser binary on remote server is only supported for Chrome/Firefox, setting will be ignored.");
    }
    return new DesiredCapabilities();
  }

  Capabilities getHeadlessCapabilities() {
    if (isChrome()) {
      ChromeOptions options = new ChromeOptions();
      options.setHeadless(headless);
      return options;
    } else if (isFirefox()) {
      FirefoxOptions options = new FirefoxOptions();
      options.setHeadless(headless);
      return options;
    } else {
      log.warning("Headless mode on remote server is only supported for Chrome/Firefox, setting will be ignored.");
    }
    return new DesiredCapabilities();
  }

  String getBrowserNameForGrid() {
    if (isLegacyFirefox()) {
      return BrowserType.FIREFOX;
    }
    else if (isIE()) {
      return BrowserType.IE;
    }
    else if (isEdge()) {
      return BrowserType.EDGE;
    }
    else if (isOpera()) {
      return BrowserType.OPERA_BLINK;
    }
    else {
      return browser;
    }
  }
}
