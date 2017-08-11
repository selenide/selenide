package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.function.Supplier;

import static com.codeborne.selenide.Configuration.browser;

class HtmlUnitDriverProcessor extends DriverProcessor {

  private final Supplier<Boolean> condition = WebDriverRunner::isHtmlUnit;
  private final DriverProcessor nextProcessor;

  HtmlUnitDriverProcessor() {
    this.nextProcessor = new EdgeDriverProcessor();
  }

  @Override
  WebDriver process(final Proxy proxy) {
    return condition.get() ? createHtmlUnitDriver(proxy) : nextProcessor.process(proxy);
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
