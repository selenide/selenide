package com.codeborne.selenide;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

public class DriverStub implements Driver {
  private final Config config;
  private final Browser browser;
  private final WebDriver webDriver;
  private final SelenideProxyServer proxy;

  public DriverStub() {
    this("zopera");
  }

  public DriverStub(String browser) {
    this(new SelenideConfig(), new Browser(browser, false), null, null);
  }

  public DriverStub(Config config, Browser browser, WebDriver webDriver, SelenideProxyServer proxy) {
    this.config = config;
    this.browser = browser;
    this.webDriver = webDriver;
    this.proxy = proxy;
  }

  @Override
  public Config config() {
    return config;
  }

  @Override
  public boolean hasWebDriverStarted() {
    return webDriver != null;
  }

  @Override
  public Browser browser() {
    return browser;
  }

  @Override
  public WebDriver getWebDriver() {
    return webDriver;
  }

  @Override
  public SelenideProxyServer getProxy() {
    return proxy;
  }

  @Override
  public WebDriver getAndCheckWebDriver() {
    return webDriver;
  }

  @Override
  public void close() {
    webDriver.close();
  }

  @Override
  public boolean supportsJavascript() {
    return hasWebDriverStarted() && webDriver instanceof JavascriptExecutor;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T executeJavaScript(String jsCode, Object... arguments) {
    return (T) ((JavascriptExecutor) webDriver).executeScript(jsCode, arguments);
  }

  @Override
  public String getUserAgent() {
    return "zhopera";
  }

  @Override
  public SelenideTargetLocator switchTo() {
    return new SelenideTargetLocator(config(), getWebDriver());
  }

  @Override
  public Actions actions() {
    return new Actions(getWebDriver());
  }
}
