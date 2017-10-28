package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.logging.Logger;

class LegacyFirefoxDriverFactory extends FirefoxDriverFactory {

  private static final Logger log = Logger.getLogger(LegacyFirefoxDriverFactory.class.getName());

  @Override
  boolean supports() {
    return WebDriverRunner.isLegacyFirefox();
  }

  @Override
  WebDriver create(final Proxy proxy) {
    log.info("Firefox 48+ is not supported by legacy Firefox driver. " +
            "Use browser=firefox with geckodriver, when using it.");

    return createLegacyFirefoxDriver(proxy);
  }

  private WebDriver createLegacyFirefoxDriver(final Proxy proxy) {
    FirefoxOptions firefoxOptions = createFirefoxOptions(proxy);
    firefoxOptions.setLegacy(true);
    return new FirefoxDriver(firefoxOptions);
  }
}
