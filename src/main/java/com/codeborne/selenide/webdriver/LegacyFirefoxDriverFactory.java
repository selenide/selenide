package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxOptions;

public class LegacyFirefoxDriverFactory extends FirefoxDriverFactory {

  @Override
  boolean supports(Config config, Browser browser) {
    return browser.isLegacyFirefox();
  }

  @Override
  public void setupBinary() {
  }

  @Override
  protected FirefoxOptions createFirefoxOptions(Config config, Browser browser, Proxy proxy) {
    FirefoxOptions firefoxOptions = super.createFirefoxOptions(config, browser, proxy);
    firefoxOptions.setLegacy(true);
    return firefoxOptions;
  }
}
