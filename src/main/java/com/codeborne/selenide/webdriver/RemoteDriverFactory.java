package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Configuration;
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

class RemoteDriverFactory extends AbstractDriverFactory {


  private static final Logger log = Logger.getLogger(RemoteDriverFactory.class.getName());

  @Override
  boolean supports(Browser browser) {
    return remote != null;
  }

  @Override
  WebDriver create(Proxy proxy) {
    return createRemoteDriver(remote, browser, proxy);
  }

  private WebDriver createRemoteDriver(final String remote, final String browser, final Proxy proxy) {
    try {
      DesiredCapabilities capabilities = getDriverCapabilities(new Browser(browser, false), proxy);
      RemoteWebDriver webDriver = new RemoteWebDriver(new URL(remote), capabilities);
      webDriver.setFileDetector(new LocalFileDetector());
      return webDriver;
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + remote, e);
    }
  }

  DesiredCapabilities getDriverCapabilities(Browser browser, Proxy proxy) {
    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    capabilities.setBrowserName(getBrowserNameForGrid(browser));
    if (headless) {
      capabilities.merge(getHeadlessCapabilities(browser));
    }
    if (!browserBinary.isEmpty()) {
      capabilities.merge(getBrowserBinaryCapabilites(browser));
    }
    return capabilities;
  }

  Capabilities getBrowserBinaryCapabilites(Browser browser) {
    log.info("Using browser binary: " + browserBinary);
    if (browser.isChrome()) {
      ChromeOptions options = new ChromeOptions();
      options.setBinary(browserBinary);
      return options;
    } else if (browser.isFirefox()) {
      FirefoxOptions options = new FirefoxOptions();
      options.setBinary(browserBinary);
      return options;
    } else {
      log.warning("Changing browser binary on remote server is only supported for Chrome/Firefox, setting will be ignored.");
    }
    return new DesiredCapabilities();
  }

  Capabilities getHeadlessCapabilities(Browser browser) {
    log.info("Starting in headless mode");
    if (browser.isChrome()) {
      ChromeOptions options = new ChromeOptions();
      options.setHeadless(headless);
      return options;
    } else if (browser.isFirefox()) {
      FirefoxOptions options = new FirefoxOptions();
      options.setHeadless(headless);
      return options;
    } else {
      log.warning("Headless mode on remote server is only supported for Chrome/Firefox, setting will be ignored.");
    }
    return new DesiredCapabilities();
  }

  String getBrowserNameForGrid(Browser browser) {
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
      return BrowserType.OPERA_BLINK;
    }
    else {
      return Configuration.browser;
    }
  }
}
