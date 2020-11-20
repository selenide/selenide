package com.codeborne.selenide;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

public interface Driver {
  Config config();
  Browser browser();
  boolean hasWebDriverStarted();
  WebDriver getWebDriver();

  @CheckReturnValue
  @Nullable
  SelenideProxyServer getProxy();

  WebDriver getAndCheckWebDriver();

  @CheckReturnValue
  @Nullable
  DownloadsFolder browserDownloadsFolder();

  void close();

  default boolean supportsJavascript() {
    return hasWebDriverStarted() && getWebDriver() instanceof JavascriptExecutor;
  }

  @SuppressWarnings("unchecked")
  default <T> T executeJavaScript(String jsCode, Object... arguments) {
    return (T) ((JavascriptExecutor) getWebDriver()).executeScript(jsCode, arguments);
  }

  @SuppressWarnings("unchecked")
  default <T> T executeAsyncJavaScript(String jsCode, Object... arguments) {
    return (T) ((JavascriptExecutor) getWebDriver()).executeAsyncScript(jsCode, arguments);
  }

  default void clearCookies() {
    if (hasWebDriverStarted()) {
      getWebDriver().manage().deleteAllCookies();
    }
  }

  default String getUserAgent() {
    return executeJavaScript("return navigator.userAgent;");
  }

  default String source() {
    return getWebDriver().getPageSource();
  }

  default String url() {
    return getWebDriver().getCurrentUrl();
  }

  default String getCurrentFrameUrl() {
    return executeJavaScript("return window.location.href").toString();
  }

  default SelenideTargetLocator switchTo() {
    return new SelenideTargetLocator(this);
  }

  default Actions actions() {
    return new Actions(getWebDriver());
  }
}
