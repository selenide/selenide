package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

class PhantomJsDriverFactory extends AbstractDriverFactory {
  @Override
  boolean supports(Config config, Browser browser) {
    return browser.isPhantomjs();
  }

  @Override
  WebDriver create(Config config, Proxy proxy) {
    return createPhantomJsDriver(config, proxy);
  }

  private WebDriver createPhantomJsDriver(Config config, Proxy proxy) {
    return createInstanceOf("org.openqa.selenium.phantomjs.PhantomJSDriver", config, proxy);
  }
}
