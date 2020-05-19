package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperaDriverFactory extends AbstractDriverFactory {
  private static final Logger log = LoggerFactory.getLogger(OperaDriverFactory.class);

  @Override
  boolean supports(Config config, Browser browser) {
    return browser.isOpera();
  }

  @Override
  public void setupWebdriverBinary() {
    if (isSystemPropertyNotSet("webdriver.opera.driver")) {
      WebDriverManager.operadriver().setup();
    }
  }

  @Override
  public WebDriver create(Config config, Browser browser, Proxy proxy) {
    return new OperaDriver(createOperaOptions(config, browser, proxy));
  }

  protected OperaOptions createOperaOptions(Config config, Browser browser, Proxy proxy) {
    OperaOptions operaOptions = new OperaOptions();
    if (config.headless()) {
      throw new InvalidArgumentException("headless browser not supported in Opera. Set headless property to false.");
    }
    if (!config.browserBinary().isEmpty()) {
      log.info("Using browser binary: {}", config.browserBinary());
      operaOptions.setBinary(config.browserBinary());
    }
    operaOptions.merge(createCommonCapabilities(config, browser, proxy));
    return operaOptions;
  }
}
