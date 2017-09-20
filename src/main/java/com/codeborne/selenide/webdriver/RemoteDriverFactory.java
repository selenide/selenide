package com.codeborne.selenide.webdriver;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.remote;
import static com.codeborne.selenide.WebDriverRunner.INTERNET_EXPLORER;
import static com.codeborne.selenide.WebDriverRunner.isIE;

class RemoteDriverFactory extends AbstractDriverFactory {

  @Override
  boolean supports() {
    return remote != null;
  }

  @Override
  WebDriver create(Proxy proxy) {
    return createRemoteDriver(remote, browser, proxy);
  }

  private WebDriver createRemoteDriver(final String remote, final String browser, final Proxy proxy) {
    try {
      DesiredCapabilities capabilities = createCommonCapabilities(proxy);
      capabilities.setBrowserName(isIE() ? INTERNET_EXPLORER : browser);
      return new RemoteWebDriver(new URL(remote), capabilities);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("Invalid 'remote' parameter: " + remote, e);
    }
  }
}
