package com.codeborne.selenide.webdriver;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Supplier;

import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.remote;
import static com.codeborne.selenide.WebDriverRunner.INTERNET_EXPLORER;
import static com.codeborne.selenide.WebDriverRunner.isIE;

class RemoteDriverProcessor extends DriverProcessor {

  private final Supplier<Boolean> condition = () -> remote != null;
  private final DriverProcessor nextProcessor;

  RemoteDriverProcessor() {
    this.nextProcessor = new ChromeDriverProcessor();
  }

  @Override
  WebDriver process(final Proxy proxy) {
    return condition.get() ?
            createRemoteDriver(remote, browser, proxy)
            : nextProcessor.process(proxy);
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
