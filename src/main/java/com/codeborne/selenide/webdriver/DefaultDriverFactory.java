package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

public class DefaultDriverFactory extends AbstractDriverFactory {
  @Override
  boolean supports(Config config, Browser browser) {
    return true;
  }

  @Override
  WebDriver create(Config config, Proxy proxy) {
    return createInstanceOf(config.browser(), config, proxy);
  }
}
