package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;

import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.browserBinary;
import static com.codeborne.selenide.Configuration.headless;

class OperaDriverFactory extends AbstractDriverFactory {

  private static final Logger log = Logger.getLogger(OperaDriverFactory.class.getName());

  @Override
  boolean supports(Browser browser) {
    return browser.isOpera();
  }

  @Override
  WebDriver create(final Proxy proxy) {
    OperaOptions operaOptions = createOperaOptions(proxy);
    return new OperaDriver(operaOptions);
  }

  OperaOptions createOperaOptions(Proxy proxy) {
    OperaOptions operaOptions = new OperaOptions();
    if (headless) {
      throw new InvalidArgumentException("headless browser not supported in Opera. Set headless property to false.");
    }
    if (!browserBinary.isEmpty()) {
      log.info("Using browser binary: " + browserBinary);
      operaOptions.setBinary(browserBinary);
    }
    operaOptions.merge(createCommonCapabilities(proxy));
    return operaOptions;
  }
}
