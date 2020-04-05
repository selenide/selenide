package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class InternetExplorerDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(InternetExplorerDriverFactory.class);

  @Override
  boolean supports(Config config, Browser browser) {
    return browser.isIE();
  }

  @Override
  WebDriver create(Config config, Proxy proxy) {
    return createInternetExplorerDriver(config, proxy);
  }

  private WebDriver createInternetExplorerDriver(Config config, Proxy proxy) {
    InternetExplorerOptions options = createExplorerOptions(config, proxy);
    return new InternetExplorerDriver(options);
  }

  InternetExplorerOptions createExplorerOptions(Config config, Proxy proxy) {
    InternetExplorerOptions options = new InternetExplorerOptions();
    setupCommonCapabilities(options, config, proxy);
    if (!config.browserBinary().isEmpty()) {
      log.info("Using browser binary: {}", config.browserBinary());
      log.warn("Changing browser binary not supported in InternetExplorer, setting will be ignored.");
    }
    config.browserOptionsInterceptors().internetExplorerOptionsInterceptor.afterSelenideChangesOptions(options);
    return options;
  }
}
