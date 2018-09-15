package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config.BrowserConfig;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

class JBrowserDriverFactory extends AbstractDriverFactory {
  @Override
  boolean supports(BrowserConfig config, Browser browser) {
    return browser.isJBrowser();
  }

  @Override
  WebDriver create(BrowserConfig config, Proxy proxy) {
    return createJBrowserDriver(config, proxy);
  }

  private WebDriver createJBrowserDriver(BrowserConfig config, Proxy proxy) {
    return createInstanceOf("com.machinepublishers.jbrowserdriver.JBrowserDriver", config, proxy);
  }
}
