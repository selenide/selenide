package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(EdgeDriverFactory.class);

  @Override
  boolean supports(Config config, Browser browser) {
    return browser.isEdge();
  }

  @Override
  public void setupWebdriverBinary() {
    if (isSystemPropertyNotSet("webdriver.edge.driver")) {
      WebDriverManager.edgedriver().setup();
    }
  }

  @Override
  public WebDriver create(Config config, Browser browser, Proxy proxy) {
    Capabilities capabilities = createCommonCapabilities(config, browser, proxy);
    EdgeOptions options = new EdgeOptions();
    options.merge(capabilities);
    if (!config.browserBinary().isEmpty()) {
      log.info("Using browser binary: {}", config.browserBinary());
      log.warn("Changing browser binary not supported in Edge, setting will be ignored.");
    }
    return new EdgeDriver(options);
  }
}
