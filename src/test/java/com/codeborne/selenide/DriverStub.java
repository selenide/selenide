package com.codeborne.selenide;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.SessionId;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.UUID.randomUUID;

/**
 * A dummy `Driver` implementation used in tests.
 */
@ParametersAreNonnullByDefault
public class DriverStub implements Driver {
  private final Config config;
  private final Browser browser;
  private final WebDriver webDriver;
  private final SelenideProxyServer proxy;
  private final DownloadsFolder browserDownloadsFolder;

  public DriverStub() {
    this("netscape navigator");
  }

  public DriverStub(String browser) {
    this(new SelenideConfig(), browser);
  }

  public DriverStub(Config config) {
    this(config, "netscape navigator");
  }

  DriverStub(Config config, String browser) {
    this(config, new Browser(browser, false), new DummyWebDriver(), null, new SharedDownloadsFolder("build/downloads/" + randomUUID()));
  }

  public DriverStub(String browser, DownloadsFolder downloadsFolder) {
    this(new SelenideConfig(), new Browser(browser, false), new DummyWebDriver(), null, downloadsFolder);
  }

  public DriverStub(Config config, @Nullable SelenideProxyServer proxy) {
    this(config, new Browser("zopera", true), new DummyWebDriver(), proxy);
  }

  public DriverStub(Config config, Browser browser,
                    WebDriver webDriver,
                    @Nullable SelenideProxyServer proxy) {
    this(config, browser, webDriver, proxy, new SharedDownloadsFolder("build/downloads/" + randomUUID()));
  }

  public DriverStub(Config config, Browser browser,
                    WebDriver webDriver,
                    @Nullable SelenideProxyServer proxy,
                    DownloadsFolder downloadsFolder) {
    this.config = config;
    this.browser = browser;
    this.webDriver = webDriver;
    this.proxy = proxy;
    this.browserDownloadsFolder = downloadsFolder;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Config config() {
    return config;
  }

  @Override
  @CheckReturnValue
  public boolean hasWebDriverStarted() {
    return webDriver != null;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Browser browser() {
    return browser;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver getWebDriver() {
    return webDriver;
  }

  @Override
  public SelenideProxyServer getProxy() {
    return proxy;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver getAndCheckWebDriver() {
    return webDriver;
  }

  @Override
  @CheckReturnValue
  @Nullable
  public DownloadsFolder browserDownloadsFolder() {
    return browserDownloadsFolder;
  }

  @Override
  public void close() {
    webDriver.close();
  }

  @Override
  @CheckReturnValue
  public boolean supportsJavascript() {
    return hasWebDriverStarted() && webDriver instanceof JavascriptExecutor;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T executeJavaScript(String jsCode, Object... arguments) {
    return (T) ((JavascriptExecutor) webDriver).executeScript(jsCode, arguments);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T executeAsyncJavaScript(String jsCode, Object... arguments) {
    return (T) ((JavascriptExecutor) webDriver).executeAsyncScript(jsCode, arguments);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String getUserAgent() {
    return "zhopera";
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public SessionId getSessionId() {
    return new SessionId("testSession");
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public SelenideTargetLocator switchTo() {
    return new SelenideTargetLocator(this);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public Actions actions() {
    return new Actions(getWebDriver());
  }
}
