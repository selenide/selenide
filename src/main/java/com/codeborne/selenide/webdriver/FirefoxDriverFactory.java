package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.logging.Logger;

class FirefoxDriverFactory extends AbstractDriverFactory {

  private static final Logger log = Logger.getLogger(FirefoxDriverFactory.class.getName());

  @Override
  boolean supports() {
    return WebDriverRunner.isFirefox();
  }

  @Override
  WebDriver create(final Proxy proxy) {
    return createFirefoxDriver(proxy);
  }

  private WebDriver createFirefoxDriver(final Proxy proxy) {
    DesiredCapabilities capabilities = createFirefoxCapabilities(proxy);
    log.info("Firefox 48+ is currently not supported by Selenium Firefox driver. " +
            "Use browser=marionette with geckodriver, when using it.");
    return new FirefoxDriver(capabilities);
  }
}
