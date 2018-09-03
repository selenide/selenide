package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Configuration.browser;

public class DefaultDriverFactory extends AbstractDriverFactory {

  @Override
  boolean supports(Browser browser) {
    return true;
  }

  @Override
  WebDriver create(final Proxy proxy) {
    return createInstanceOf(browser, proxy);
  }
}
