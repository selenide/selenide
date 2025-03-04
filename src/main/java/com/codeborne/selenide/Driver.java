package com.codeborne.selenide;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import static com.codeborne.selenide.impl.JavaScript.asJsExecutor;
import static com.codeborne.selenide.impl.JavaScript.jsExecutor;
import static java.util.Objects.requireNonNull;

public interface Driver {
  Config config();

  Browser browser();

  boolean hasWebDriverStarted();

  WebDriver getWebDriver();

  SelenideProxyServer getProxy();

  WebDriver getAndCheckWebDriver();

  @Nullable
  DownloadsFolder browserDownloadsFolder();

  void close();

  default boolean supportsJavascript() {
    return hasWebDriverStarted() && asJsExecutor(getWebDriver()).isPresent();
  }

  @Nullable
  @CanIgnoreReturnValue
  @SuppressWarnings("unchecked")
  default <T> T executeJavaScript(String jsCode, Object... arguments) {
    return (T) jsExecutor(getWebDriver()).executeScript(jsCode, arguments);
  }

  @Nullable
  @CanIgnoreReturnValue
  @SuppressWarnings("unchecked")
  default <T> T executeAsyncJavaScript(String jsCode, Object... arguments) {
    return (T) jsExecutor(getWebDriver()).executeAsyncScript(jsCode, arguments);
  }

  default void clearCookies() {
    if (hasWebDriverStarted()) {
      getWebDriver().manage().deleteAllCookies();
    }
  }

  default String getUserAgent() {
    return requireNonNull(executeJavaScript("return navigator.userAgent;"));
  }

  @Nullable
  default String source() {
    return getWebDriver().getPageSource();
  }

  default String url() {
    return requireNonNull(getWebDriver().getCurrentUrl());
  }

  default String getCurrentFrameUrl() {
    return requireNonNull(executeJavaScript("return window.location.href")).toString();
  }

  default SelenideTargetLocator switchTo() {
    return new SelenideTargetLocator(this);
  }

  default Actions actions() {
    return new Actions(getWebDriver());
  }

  default SessionId getSessionId() {
    WebDriver driver = getWebDriver();
    if (driver instanceof WrapsDriver wrapper) {
      driver = wrapper.getWrappedDriver();
    }
    return ((RemoteWebDriver) driver).getSessionId();
  }

  default boolean isLocalBrowser() {
    return config().remote() == null;
  }
}
