package com.codeborne.selenide.webdriver;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Configuration.browser;

public class DefaultDriverFactory extends AbstractDriverFactory {

  @Override
  boolean supports() {
    return true;
  }

  @Override
  WebDriver create(final Proxy proxy) {
    return createInstanceOf(browser, proxy);
  }
}
