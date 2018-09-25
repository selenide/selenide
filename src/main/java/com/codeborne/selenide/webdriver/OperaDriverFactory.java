package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;

import java.util.logging.Logger;

class OperaDriverFactory extends AbstractDriverFactory {
  private static final Logger log = Logger.getLogger(OperaDriverFactory.class.getName());

  @Override
  boolean supports(Config config, Browser browser) {
    return browser.isOpera();
  }

  @Override
  WebDriver create(Config config, Proxy proxy) {
    OperaOptions operaOptions = createOperaOptions(config, proxy);
    return new OperaDriver(operaOptions);
  }

  OperaOptions createOperaOptions(Config config, Proxy proxy) {
    OperaOptions operaOptions = new OperaOptions();
    if (config.headless()) {
      throw new InvalidArgumentException("headless browser not supported in Opera. Set headless property to false.");
    }
    if (!config.browserBinary().isEmpty()) {
      log.info("Using browser binary: " + config.browserBinary());
      operaOptions.setBinary(config.browserBinary());
    }
    operaOptions.merge(createCommonCapabilities(config, proxy));
    return operaOptions;
  }
}
