package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import static com.codeborne.selenide.Configuration.browser;

class HtmlUnitDriverFactory extends AbstractDriverFactory {

  @Override
  boolean supports(Browser browser) {
    return browser.isHtmlUnit();
  }

  @Override
  WebDriver create(final Proxy proxy) {
    return createHtmlUnitDriver(proxy);
  }

  private WebDriver createHtmlUnitDriver(final Proxy proxy) {
    DesiredCapabilities capabilities = DesiredCapabilities.htmlUnit();
    capabilities.merge(createCommonCapabilities(proxy));
    capabilities.setCapability(HtmlUnitDriver.INVALIDSELECTIONERROR, true);
    capabilities.setCapability(HtmlUnitDriver.INVALIDXPATHERROR, false);
    if (browser.indexOf(':') > -1) {
      // Use constants BrowserType.IE, BrowserType.FIREFOX, BrowserType.CHROME etc.
      String emulatedBrowser = browser.replaceFirst("htmlunit:(.*)", "$1");
      capabilities.setVersion(emulatedBrowser);
    }
    return new HtmlUnitDriver(capabilities);
  }
}
