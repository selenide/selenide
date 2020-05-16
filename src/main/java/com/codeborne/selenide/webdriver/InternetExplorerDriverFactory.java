package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternetExplorerDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(InternetExplorerDriverFactory.class);

  @Override
  boolean supports(Config config, Browser browser) {
    return browser.isIE();
  }

  @Override
  public void setupBinary() {
    if (isSystemPropertyNotSet("webdriver.ie.driver")) {
      WebDriverManager.iedriver().setup();
    }
  }

  @Override
  public WebDriver create(Config config, Browser browser, Proxy proxy) {
    Capabilities capabilities = createCommonCapabilities(config, browser, proxy);
    InternetExplorerOptions options = new InternetExplorerOptions(capabilities);
    if (!config.browserBinary().isEmpty()) {
      log.info("Using browser binary: {}", config.browserBinary());
      log.warn("Changing browser binary not supported in InternetExplorer, setting will be ignored.");
    }
    return new InternetExplorerDriver(options);
  }
}
