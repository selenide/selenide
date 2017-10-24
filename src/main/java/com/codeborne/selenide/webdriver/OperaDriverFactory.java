package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.opera.OperaOptions;

import static com.codeborne.selenide.Configuration.browserBinary;

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
    operaOptions.setBinary(browserBinary);
    operaOptions.merge(createCommonCapabilities(proxy));
    return new OperaDriver(operaOptions);
  }
}
