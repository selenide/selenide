package com.codeborne.selenide;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.SessionId;

import static java.util.Objects.requireNonNull;
import static java.util.UUID.randomUUID;
import static org.mockito.Mockito.mock;

/**
 * A dummy `Driver` implementation used in tests.
 */
public class DriverStub implements Driver {
  private final Config config;
  private final Browser browser;
  private final WebDriver webDriver;
  @Nullable
  private final SelenideProxyServer proxy;
  private final BrowserDownloadsFolder browserDownloadsFolder;
  private final Actions actionsMock = mock();

  public DriverStub() {
    this("netscape navigator");
  }

  public DriverStub(WebDriver webDriver) {
    this(new SelenideConfig(), webDriver);
  }

  public DriverStub(Config config, WebDriver webDriver) {
    this(config, new Browser("netscape navigator", false), webDriver, null);
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

  public DriverStub(String browser, BrowserDownloadsFolder downloadsFolder) {
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
                    BrowserDownloadsFolder downloadsFolder) {
    this.config = config;
    this.browser = browser;
    this.webDriver = webDriver;
    this.proxy = proxy;
    this.browserDownloadsFolder = downloadsFolder;
  }

  @Override
  public Config config() {
    return config;
  }

  @Override
  public boolean hasWebDriverStarted() {
    return true;
  }

  @Override
  public Browser browser() {
    return browser;
  }

  @Override
  public WebDriver getWebDriver() {
    return requireNonNull(webDriver);
  }

  @Override
  public SelenideProxyServer getProxy() {
    if (!config.proxyEnabled()) {
      throw new IllegalStateException("Proxy server is not enabled. You need to set proxyEnabled=true before opening a browser.");
    }
    if (proxy == null) {
      throw new IllegalStateException("config.proxyEnabled == true but proxy server is not created.");
    }

    return proxy;
  }

  @Override
  public WebDriver getAndCheckWebDriver() {
    return requireNonNull(webDriver);
  }

  @Override
  @Nullable
  public BrowserDownloadsFolder browserDownloadsFolder() {
    return browserDownloadsFolder;
  }

  @Override
  public void close() {
    webDriver.close();
  }

  @Override
  public boolean supportsJavascript() {
    return hasWebDriverStarted() && webDriver instanceof JavascriptExecutor;
  }

  @Nullable
  @CanIgnoreReturnValue
  @Override
  @SuppressWarnings("unchecked")
  public <T> T executeJavaScript(String jsCode, Object... arguments) {
    return (T) ((JavascriptExecutor) webDriver).executeScript(jsCode, arguments);
  }

  @Nullable
  @CanIgnoreReturnValue
  @Override
  @SuppressWarnings("unchecked")
  public <T> T executeAsyncJavaScript(String jsCode, Object... arguments) {
    return (T) ((JavascriptExecutor) webDriver).executeAsyncScript(jsCode, arguments);
  }

  @Override
  public String getUserAgent() {
    return "zhopera";
  }

  @Override
  public SessionId getSessionId() {
    return new SessionId("testSession");
  }

  @Override
  public Actions actions() {
    return actionsMock;
  }
}
