package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config.BrowserConfig;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.logging.Logger;

class LegacyFirefoxDriverFactory extends FirefoxDriverFactory {
  private static final Logger log = Logger.getLogger(LegacyFirefoxDriverFactory.class.getName());

  @Override
  boolean supports(BrowserConfig config, Browser browser) {
    return browser.isLegacyFirefox();
  }

  @Override
  WebDriver create(BrowserConfig config, Proxy proxy) {
    String logFilePath = System.getProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
    System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, logFilePath);
    return createLegacyFirefoxDriver(config, proxy);
  }

  private WebDriver createLegacyFirefoxDriver(BrowserConfig config, Proxy proxy) {
    FirefoxOptions firefoxOptions = createFirefoxOptions(config, proxy);
    firefoxOptions.setLegacy(true);
    return new FirefoxDriver(firefoxOptions);
  }
}
