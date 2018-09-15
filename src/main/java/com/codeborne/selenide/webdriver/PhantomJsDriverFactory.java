package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config.BrowserConfig;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

class PhantomJsDriverFactory extends AbstractDriverFactory {
  @Override
  boolean supports(BrowserConfig config, Browser browser) {
    return browser.isPhantomjs();
  }

  @Override
  WebDriver create(BrowserConfig config, Proxy proxy) {
    return createPhantomJsDriver(config, proxy);
  }

  private WebDriver createPhantomJsDriver(BrowserConfig config, Proxy proxy) {
    return createInstanceOf("org.openqa.selenium.phantomjs.PhantomJSDriver", config, proxy);
  }
}
