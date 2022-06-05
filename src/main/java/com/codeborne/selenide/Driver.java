package com.codeborne.selenide;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WrapsDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.slf4j.LoggerFactory.getLogger;

@ParametersAreNonnullByDefault
public interface Driver {
  @CheckReturnValue
  @Nonnull
  Config config();

  @CheckReturnValue
  @Nonnull
  Browser browser();

  @CheckReturnValue
  boolean hasWebDriverStarted();

  @CheckReturnValue
  @Nonnull
  WebDriver getWebDriver();

  @CheckReturnValue
  @Nullable
  SelenideProxyServer getProxy();

  @CheckReturnValue
  @Nonnull
  WebDriver getAndCheckWebDriver();

  @CheckReturnValue
  @Nullable
  DownloadsFolder browserDownloadsFolder();

  void close();

  @CheckReturnValue
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

  @CheckReturnValue
  @Nonnull
  default String getUserAgent() {
    return executeJavaScript("return navigator.userAgent;");
  }

  @Nonnull
  default Platform getPlatform() {
    if (!(getWebDriver() instanceof JavascriptExecutor)) {
      return Platform.UNKNOWN;
    }
    try {
      String platform = executeJavaScript("return navigator.platform");
      if (platform == null || platform.trim().isEmpty()) {
        getLogger(getClass()).warn("Cannot detect platform: navigator.platform returned \"{}\"", platform);
      }
      return new Platform(platform);
    }
    catch (WebDriverException cannotExecuteJavascript) {
      getLogger(getClass()).debug("Cannot get navigator.platform", cannotExecuteJavascript);
      return Platform.UNKNOWN;
    }
  }

  @CheckReturnValue
  @Nonnull
  default String source() {
    return getWebDriver().getPageSource();
  }

  @CheckReturnValue
  @Nonnull
  default String url() {
    return getWebDriver().getCurrentUrl();
  }

  @CheckReturnValue
  @Nonnull
  default String getCurrentFrameUrl() {
    return executeJavaScript("return window.location.href").toString();
  }

  @CheckReturnValue
  @Nonnull
  default SelenideTargetLocator switchTo() {
    return new SelenideTargetLocator(this);
  }

  @CheckReturnValue
  @Nonnull
  default Actions actions() {
    return new Actions(getWebDriver());
  }

  @CheckReturnValue
  @Nonnull
  default SessionId getSessionId() {
    WebDriver driver = getWebDriver();
    if (driver instanceof WrapsDriver) {
      driver = ((WrapsDriver) driver).getWrappedDriver();
    }
    return ((RemoteWebDriver) driver).getSessionId();
  }
}
