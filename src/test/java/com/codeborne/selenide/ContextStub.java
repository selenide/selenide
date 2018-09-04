package com.codeborne.selenide;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

public class ContextStub implements Context {
  private final Browser browser;
  private final WebDriver webDriver;
  private final SelenideProxyServer proxy;

  public ContextStub(String browser) {
    this(new Browser(browser, false), null, null);
  }

  public ContextStub(Browser browser) {
    this(browser, null, null);
  }

  public ContextStub(Browser browser, WebDriver webDriver, SelenideProxyServer proxy) {
    this.browser = browser;
    this.webDriver = webDriver;
    this.proxy = proxy;
  }

  public boolean hasWebDriverStarted() {
    return webDriver != null;
  }

  public Browser getBrowser() {
    return browser;
  }

  public WebDriver getWebDriver() {
    return webDriver;
  }

  public SelenideProxyServer getProxy() {
    return proxy;
  }

  public boolean supportsJavascript() {
    return hasWebDriverStarted() && webDriver instanceof JavascriptExecutor;
  }

  @SuppressWarnings("unchecked")
  public <T> T executeJavaScript(String jsCode, Object... arguments) {
    return (T) ((JavascriptExecutor) webDriver).executeScript(jsCode, arguments);
  }

  public SelenideTargetLocator switchTo() {
    return new SelenideTargetLocator(getWebDriver());
  }

  public Actions actions() {
    return new Actions(getWebDriver());
  }
}
