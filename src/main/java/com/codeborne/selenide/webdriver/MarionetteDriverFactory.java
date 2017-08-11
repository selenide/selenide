package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.logging.Logger;

class MarionetteDriverFactory extends AbstractDriverFactory {

  private static final Logger log = Logger.getLogger(MarionetteDriverFactory.class.getName());

  @Override
  boolean supports() {
    return WebDriverRunner.isMarionette();
  }

  @Override
  WebDriver create(final Proxy proxy) {
    return createMarionetteDriver(proxy);
  }

  private WebDriver createMarionetteDriver(final Proxy proxy) {
    DesiredCapabilities capabilities = createFirefoxCapabilities(proxy);
    capabilities.setCapability("marionette", true);
    return new FirefoxDriver(capabilities);
  }
}
