package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.browserBinary;

class InternetExplorerDriverFactory extends AbstractDriverFactory {

  private static final Logger log = Logger.getLogger(InternetExplorerDriverFactory.class.getName());

  @Override
  boolean supports(Browser browser) {
    return browser.isIE();
  }

  @Override
  WebDriver create(final Proxy proxy) {
    return createInternetExplorerDriver(proxy);
  }

  private WebDriver createInternetExplorerDriver(final Proxy proxy) {
    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    InternetExplorerOptions options = new InternetExplorerOptions(capabilities);
    if (!browserBinary.isEmpty()) {
      log.info("Using browser binary: " + browserBinary);
      log.warning("Changing browser binary not supported in InternetExplorer, setting will be ignored.");
    }
    return new InternetExplorerDriver(options);
  }
}
