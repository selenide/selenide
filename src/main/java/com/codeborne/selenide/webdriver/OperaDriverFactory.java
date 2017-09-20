package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

class OperaDriverFactory extends AbstractDriverFactory {

  @Override
  boolean supports() {
    return WebDriverRunner.isOpera();
  }

  @Override
  WebDriver create(final Proxy proxy) {
    return createOperaDriver(proxy);
  }

  private WebDriver createOperaDriver(final Proxy proxy) {
    return createInstanceOf("org.openqa.selenium.opera.OperaDriver", proxy);
  }
}
