package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;

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
    OperaOptions operaOptions = new OperaOptions();
    operaOptions.merge(createCommonCapabilities(proxy));
    return new OperaDriver(operaOptions);
  }
}
