package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config.BrowserConfig;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

class HtmlUnitDriverFactory extends AbstractDriverFactory {

  @Override
  boolean supports(BrowserConfig config, Browser browser) {
    return browser.isHtmlUnit();
  }

  @Override
  WebDriver create(BrowserConfig config, Proxy proxy) {
    return createHtmlUnitDriver(config, proxy);
  }

  private WebDriver createHtmlUnitDriver(BrowserConfig config, Proxy proxy) {
    DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
    capabilities.merge(createCommonCapabilities(config, proxy));
    capabilities.setCapability(HtmlUnitDriver.INVALIDSELECTIONERROR, true);
    capabilities.setCapability(HtmlUnitDriver.INVALIDXPATHERROR, false);
    if (config.browser().indexOf(':') > -1) {
      // Use constants BrowserType.IE, BrowserType.FIREFOX, BrowserType.CHROME etc.
      String emulatedBrowser = config.browser().replaceFirst("htmlunit:(.*)", "$1");
      capabilities.setVersion(emulatedBrowser);
    }
    return new HtmlUnitDriver(capabilities);
  }
}
