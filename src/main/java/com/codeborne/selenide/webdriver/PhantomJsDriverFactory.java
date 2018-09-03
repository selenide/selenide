package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

class PhantomJsDriverFactory extends AbstractDriverFactory {

  @Override
  boolean supports(Browser browser) {
    return browser.isPhantomjs();
  }

  @Override
  WebDriver create(final Proxy proxy) {
    return createPhantomJsDriver(proxy);
  }

  private WebDriver createPhantomJsDriver(final Proxy proxy) {
    return createInstanceOf("org.openqa.selenium.phantomjs.PhantomJSDriver", proxy);
  }
}
