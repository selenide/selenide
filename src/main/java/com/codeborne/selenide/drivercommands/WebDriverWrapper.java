package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Configuration.holdBrowserOpen;

public class WebDriverWrapper implements Driver {
  private final WebDriver webDriver;
  private final Config config;

  public WebDriverWrapper(Config config, WebDriver webDriver) {
    this.config = config;
    this.webDriver = webDriver;
  }

  @Override
  public Config config() {
    return config;
  }

  @Override
  public Browser browser() {
    return new Browser(config.browser().browser(), config.browser().headless());
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
