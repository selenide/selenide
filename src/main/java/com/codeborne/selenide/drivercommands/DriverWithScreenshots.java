package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.remote.SessionId;

import java.util.List;

/**
 * Associates a screenshot laboratory with a driver without changing the delegate's state.
 * Uses {@link Driver#switchTo()} so switching failures retain this screenshot context.
 */
public final class DriverWithScreenshots implements Driver {
  private final Driver delegate;
  private final ScreenShotLaboratory screenshots;

  public DriverWithScreenshots(Driver delegate, ScreenShotLaboratory screenshots) {
    this.delegate = delegate;
    this.screenshots = screenshots;
  }

  @Override
  public ScreenShotLaboratory screenshots() {
    return screenshots;
  }

  @Override
  public Config config() {
    return delegate.config();
  }

  @Override
  public Browser browser() {
    return delegate.browser();
  }

  @Override
  public boolean hasWebDriverStarted() {
    return delegate.hasWebDriverStarted();
  }

  @Override
  public WebDriver getWebDriver() {
    return delegate.getWebDriver();
  }

  @Override
  public SelenideProxyServer getProxy() {
    return delegate.getProxy();
  }

  @Override
  public WebDriver getAndCheckWebDriver() {
    return delegate.getAndCheckWebDriver();
  }

  @Nullable
  @Override
  public DownloadsFolder browserDownloadsFolder() {
    return delegate.browserDownloadsFolder();
  }

  @Override
  public List<LogEntry> getBrowserLogs() {
    return delegate.getBrowserLogs();
  }

  @Override
  public void close() {
    delegate.close();
  }

  @Override
  public boolean supportsJavascript() {
    return delegate.supportsJavascript();
  }

  @Nullable
  @Override
  public <T> T executeJavaScript(String jsCode, Object... arguments) {
    return delegate.executeJavaScript(jsCode, arguments);
  }

  @Nullable
  @Override
  public <T> T executeAsyncJavaScript(String jsCode, Object... arguments) {
    return delegate.executeAsyncJavaScript(jsCode, arguments);
  }

  @Override
  public void clearCookies() {
    delegate.clearCookies();
  }

  @Override
  public String getUserAgent() {
    return delegate.getUserAgent();
  }

  @Nullable
  @Override
  public String source() {
    return delegate.source();
  }

  @Override
  public String url() {
    return delegate.url();
  }

  @Override
  public String getCurrentFrameUrl() {
    return delegate.getCurrentFrameUrl();
  }

  @Override
  public Actions actions() {
    return delegate.actions();
  }

  @Override
  public SessionId getSessionId() {
    return delegate.getSessionId();
  }

  @Override
  public boolean isLocalBrowser() {
    return delegate.isLocalBrowser();
  }
}
