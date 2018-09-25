package com.codeborne.selenide;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

public interface Driver {
  Config config();
  Browser browser();
  WebDriver getWebDriver();
  SelenideProxyServer getProxy();
  WebDriver getAndCheckWebDriver();
  void close();

  default boolean hasWebDriverStarted() {
    return getWebDriver() != null;
  }

  default boolean supportsJavascript() {
    return hasWebDriverStarted() && getWebDriver() instanceof JavascriptExecutor;
  }

  @SuppressWarnings("unchecked")
  default <T> T executeJavaScript(String jsCode, Object... arguments) {
    return (T) ((JavascriptExecutor) getWebDriver()).executeScript(jsCode, arguments);
  }

  default String getUserAgent() {
    return executeJavaScript("return navigator.userAgent;");
  }

  default SelenideTargetLocator switchTo() {
    return new SelenideTargetLocator(config(), getWebDriver());
  }

  default Actions actions() {
    return new Actions(getWebDriver());
  }
}
