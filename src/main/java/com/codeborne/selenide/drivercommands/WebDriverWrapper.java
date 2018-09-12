package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Configuration.holdBrowserOpen;

public class WebDriverWrapper implements Driver {
  private final WebDriver webDriver;

  public WebDriverWrapper(WebDriver webDriver) {
    this.webDriver = webDriver;
  }

  @Override
  public Browser browser() {
    return new Browser(Configuration.browser, Configuration.headless); // TODO Not sure...
  }

  @Override
  public WebDriver getWebDriver() {
    return webDriver;
  }

  @Override
  public SelenideProxyServer getProxy() {
    return null;
  }

  @Override
  public WebDriver getAndCheckWebDriver() {
    return webDriver;
  }

  @Override
  public void close() {
    if (!holdBrowserOpen) {
      new CloseDriverCommand(webDriver, null).run();
    }
  }
}
