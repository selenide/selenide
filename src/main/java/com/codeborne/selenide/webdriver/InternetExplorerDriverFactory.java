package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.logging.Logger;

class InternetExplorerDriverFactory extends AbstractDriverFactory {
  private static final Logger log = Logger.getLogger(InternetExplorerDriverFactory.class.getName());

  @Override
  boolean supports(Config config, Browser browser) {
    return browser.isIE();
  }

  @Override
  WebDriver create(Config config, Proxy proxy) {
    return createInternetExplorerDriver(config, proxy);
  }

  private WebDriver createInternetExplorerDriver(Config config, Proxy proxy) {
    DesiredCapabilities capabilities = createCommonCapabilities(config, proxy);
    InternetExplorerOptions options = new InternetExplorerOptions(capabilities);
    if (!config.browserBinary().isEmpty()) {
      log.info("Using browser binary: " + config.browserBinary());
      log.warning("Changing browser binary not supported in InternetExplorer, setting will be ignored.");
    }
    return new InternetExplorerDriver(options);
  }
}
