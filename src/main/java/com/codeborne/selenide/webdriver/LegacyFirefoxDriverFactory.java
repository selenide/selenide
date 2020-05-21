package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.firefox.FirefoxOptions;

public class LegacyFirefoxDriverFactory extends FirefoxDriverFactory {

  @Override
  public void setupWebdriverBinary() {
  }

  @Override
  public FirefoxOptions createCapabilities(Config config, Browser browser, Proxy proxy) {
    FirefoxOptions firefoxOptions = super.createCapabilities(config, browser, proxy);
    firefoxOptions.setLegacy(true);
    return firefoxOptions;
  }
}
