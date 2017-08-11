package com.codeborne.selenide.webdriver;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Configuration.browser;

class DefaultDriverProcessor extends DriverProcessor {
  @Override
  WebDriver process(final Proxy proxy) {
    return createInstanceOf(browser, proxy);
  }
}
