package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class RemoteDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(RemoteDriverFactory.class);

  @Override
  boolean supports(Config config, Browser browser) {
    return config.remote() != null;
  }

  @Override
  public void setupWebdriverBinary() {
  }

  @Override
  public WebDriver create(Config config, Browser browser, Proxy proxy) {
    try {
      Capabilities capabilities = getDriverCapabilities(config, browser, proxy);
      RemoteWebDriver webDriver = new RemoteWebDriver(new URL(config.remote()), capabilities);
      webDriver.setFileDetector(new LocalFileDetector());
      return webDriver;
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + config.remote(), e);
    }
  }

  protected Capabilities getDriverCapabilities(Config config, Browser browser, Proxy proxy) {
    MutableCapabilities capabilities = createCommonCapabilities(config, browser, proxy);
    capabilities.setCapability(CapabilityType.BROWSER_NAME, getBrowserNameForGrid(config, browser));
    if (config.headless()) {
      capabilities.merge(getHeadlessCapabilities(config, browser));
    }
    if (!config.browserBinary().isEmpty()) {
      capabilities.merge(getBrowserBinaryCapabilities(config, browser));
    }
    return capabilities;
  }

  protected Capabilities getBrowserBinaryCapabilities(Config config, Browser browser) {
    log.info("Using browser binary: {}", config.browserBinary());
    if (browser.isChrome()) {
      ChromeOptions options = new ChromeOptions();
      options.setBinary(config.browserBinary());
      return options;
    } else if (browser.isFirefox()) {
      FirefoxOptions options = new FirefoxOptions();
      options.setBinary(config.browserBinary());
      return options;
    } else {
      log.warn("Changing browser binary on remote server is only supported for Chrome/Firefox, setting will be ignored.");
    }
    return new DesiredCapabilities();
  }

  protected Capabilities getHeadlessCapabilities(Config config, Browser browser) {
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
      log.warn("Headless mode on remote server is only supported for Chrome/Firefox, setting will be ignored.");
    }
    return new DesiredCapabilities();
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
      return BrowserType.OPERA_BLINK;
    }
    else {
      return config.browser();
    }
  }
}
