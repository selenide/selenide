package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

class SafariDriverFactory extends AbstractDriverFactory {

  @Override
  boolean supports() {
    return WebDriverRunner.isSafari();
  }

  @Override
  WebDriver create(final Proxy proxy) {
    return createSafariDriver(proxy);
  }

  private WebDriver createSafariDriver(final Proxy proxy) {
    return createInstanceOf("org.openqa.selenium.safari.SafariDriver", proxy);
  }
}
