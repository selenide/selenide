package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
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

class RemoteDriverFactory extends AbstractDriverFactory {
  private static final Logger log = Logger.getLogger(RemoteDriverFactory.class.getName());

  @Override
  boolean supports(Config config, Browser browser) {
    return config.remote() != null;
  }

  @Override
  WebDriver create(Config config, Proxy proxy) {
    return createRemoteDriver(config, proxy);
  }

  private WebDriver createRemoteDriver(Config config, Proxy proxy) {
    try {
      DesiredCapabilities capabilities = getDriverCapabilities(config, new Browser(config.browser(), false), proxy);
      RemoteWebDriver webDriver = new RemoteWebDriver(new URL(config.remote()), capabilities);
      webDriver.setFileDetector(new LocalFileDetector());
      return webDriver;
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + config.remote(), e);
    }
  }

  DesiredCapabilities getDriverCapabilities(Config config, Browser browser, Proxy proxy) {
    DesiredCapabilities capabilities = createCommonCapabilities(config, proxy);
    capabilities.setBrowserName(getBrowserNameForGrid(config, browser));
    if (config.headless()) {
      capabilities.merge(getHeadlessCapabilities(config, browser));
    }
    if (!config.browserBinary().isEmpty()) {
      capabilities.merge(getBrowserBinaryCapabilities(config, browser));
    }
    return capabilities;
  }

  Capabilities getBrowserBinaryCapabilities(Config config, Browser browser) {
    log.info("Using browser binary: " + config.browserBinary());
    if (browser.isChrome()) {
      ChromeOptions options = new ChromeOptions();
      options.setBinary(config.browserBinary());
      return options;
    } else if (browser.isFirefox()) {
      FirefoxOptions options = new FirefoxOptions();
      options.setBinary(config.browserBinary());
      return options;
    } else {
      log.warning("Changing browser binary on remote server is only supported for Chrome/Firefox, setting will be ignored.");
    }
    return new DesiredCapabilities();
  }

  private Capabilities getHeadlessCapabilities(Config config, Browser browser) {
    log.info("Starting in headless mode");
    if (browser.isChrome()) {
      ChromeOptions options = new ChromeOptions();
      options.setHeadless(config.headless());
      return options;
    } else if (browser.isFirefox()) {
      FirefoxOptions options = new FirefoxOptions();
      options.setHeadless(config.headless());
      return options;
    } else {
      log.warning("Headless mode on remote server is only supported for Chrome/Firefox, setting will be ignored.");
    }
    return new DesiredCapabilities();
  }

  String getBrowserNameForGrid(Config config, Browser browser) {
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
      return config.browser();
    }
  }
}
