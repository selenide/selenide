package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

class InternetExplorerDriverFactory extends AbstractDriverFactory {

  @Override
  boolean supports() {
    return WebDriverRunner.isIE();
  }

  @Override
  WebDriver create(final Proxy proxy) {
    return createInternetExplorerDriver(proxy);
  }

  private WebDriver createInternetExplorerDriver(final Proxy proxy) {
    DesiredCapabilities capabilities = createCommonCapabilities(proxy);
    return new InternetExplorerDriver(capabilities);
  }
}
