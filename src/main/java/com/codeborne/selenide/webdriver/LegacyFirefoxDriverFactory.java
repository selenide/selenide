package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.logging.Logger;

class LegacyFirefoxDriverFactory extends FirefoxDriverFactory {

  private static final Logger log = Logger.getLogger(LegacyFirefoxDriverFactory.class.getName());

  @Override
  boolean supports(Browser browser) {
    return browser.isLegacyFirefox();
  }

  @Override
  WebDriver create(final Proxy proxy) {
    String logFilePath = System.getProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "/dev/null");
    System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, logFilePath);
    return createLegacyFirefoxDriver(proxy);
  }

  private WebDriver createLegacyFirefoxDriver(final Proxy proxy) {
    FirefoxOptions firefoxOptions = createFirefoxOptions(proxy);
    firefoxOptions.setLegacy(true);
    return new FirefoxDriver(firefoxOptions);
  }
}
