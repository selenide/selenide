package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

class JBrowserDriverFactory extends AbstractDriverFactory {
  @Override
  boolean supports(Config config, Browser browser) {
    return browser.isJBrowser();
  }

  @Override
  WebDriver create(Config config, Proxy proxy) {
    return createJBrowserDriver(config, proxy);
  }

  private WebDriver createJBrowserDriver(Config config, Proxy proxy) {
    return createInstanceOf("com.machinepublishers.jbrowserdriver.JBrowserDriver", config, proxy);
  }
}
