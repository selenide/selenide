package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.browserBinary;

class SafariDriverFactory extends AbstractDriverFactory {

  private static final Logger log = Logger.getLogger(SafariDriverFactory.class.getName());

  @Override
  boolean supports(Browser browser) {
    return browser.isSafari();
  }

  @Override
  WebDriver create(final Proxy proxy) {
    return createSafariDriver(proxy);
  }

  private WebDriver createSafariDriver(final Proxy proxy) {
    if (!browserBinary.isEmpty()) {
      log.info("Using browser binary: " + browserBinary);
      log.warning("Changing browser binary not supported in Safari, setting will be ignored.");
    }
    return createInstanceOf("org.openqa.selenium.safari.SafariDriver", proxy);
  }
}
