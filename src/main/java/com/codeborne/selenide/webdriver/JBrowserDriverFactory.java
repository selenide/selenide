package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

class JBrowserDriverFactory extends AbstractDriverFactory {

  @Override
  boolean supports(Browser browser) {
    return browser.isJBrowser();
  }

  @Override
  WebDriver create(final Proxy proxy) {
    return createJBrowserDriver(proxy);
  }

  private WebDriver createJBrowserDriver(final Proxy proxy) {
    return createInstanceOf("com.machinepublishers.jbrowserdriver.JBrowserDriver", proxy);
  }
}
