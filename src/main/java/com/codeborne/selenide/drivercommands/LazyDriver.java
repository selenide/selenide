package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
import org.openqa.selenium.support.events.WebDriverListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.currentThread;

/**
 * A `Driver` implementation which opens browser on demand (on a first call).
 * May be created with its own config, proxy and listeners.
 */
@ParametersAreNonnullByDefault
public class LazyDriver implements Driver {
  private static final Logger log = LoggerFactory.getLogger(LazyDriver.class);

  private final Config config;
  private final BrowserHealthChecker browserHealthChecker;
  private final WebDriverFactory factory;
  private final CloseDriverCommand closeDriverCommand;
  private final CreateDriverCommand createDriverCommand;
  private final Proxy userProvidedProxy;
  private final List<WebDriverEventListener> eventListeners;
  private final List<WebDriverListener> listeners;
  private final Browser browser;

  private boolean closed;
  private WebDriver webDriver;
  @Nullable
  private SelenideProxyServer selenideProxyServer;
  @Nullable
  private DownloadsFolder browserDownloadsFolder;

  public LazyDriver(Config config, @Nullable Proxy userProvidedProxy,
                    List<WebDriverEventListener> eventListeners, List<WebDriverListener> listeners) {
    this(config, userProvidedProxy, eventListeners, listeners, new WebDriverFactory(), new BrowserHealthChecker(),
      new CreateDriverCommand(), new CloseDriverCommand());
  }

  LazyDriver(Config config, @Nullable Proxy userProvidedProxy,
             List<WebDriverEventListener> eventListeners, List<WebDriverListener> listeners,
             WebDriverFactory factory, BrowserHealthChecker browserHealthChecker,
             CreateDriverCommand createDriverCommand, CloseDriverCommand closeDriverCommand) {
    this.config = config;
    this.browser = new Browser(config.browser(), config.headless());
    this.userProvidedProxy = userProvidedProxy;
    this.eventListeners = new ArrayList<>(eventListeners);
    this.listeners = new ArrayList<>(listeners);
    this.factory = factory;
    this.browserHealthChecker = browserHealthChecker;
    this.closeDriverCommand = closeDriverCommand;
    this.createDriverCommand = createDriverCommand;
  }

  @Override
  @Nonnull
  public Config config() {
    return config;
  }

  @Override
  @Nonnull
  public Browser browser() {
    return browser;
  }

  @Override
  public boolean hasWebDriverStarted() {
    return webDriver != null;
  }

  @Override
  @Nonnull
  public synchronized WebDriver getWebDriver() {
    if (closed) {
      throw new IllegalStateException("Webdriver has been closed. You need to call open(url) to open a browser again.");
    }
    if (webDriver == null) {
      throw new IllegalStateException("No webdriver is bound to current thread: " + currentThread().getId() +
        ". You need to call open(url) first.");
    }
    return webDriver;
  }

  @Override
  @Nullable
  public SelenideProxyServer getProxy() {
    return selenideProxyServer;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public synchronized WebDriver getAndCheckWebDriver() {
    if (webDriver != null && !browserHealthChecker.isBrowserStillOpen(webDriver)) {
      if (config.reopenBrowserOnFail()) {
        log.info("Webdriver has been closed meanwhile. Let's re-create it.");
        close();
        createDriver();
      }
      else {
        close();
        throw new IllegalStateException("Webdriver for current thread: " + currentThread().getId() +
          " has been closed meanwhile, and cannot create a new webdriver because reopenBrowserOnFail=false");
      }
    }
    else if (webDriver == null) {
      log.info("No webdriver is bound to current thread: {} - let's create a new webdriver", currentThread().getId());
      createDriver();
    }
    return getWebDriver();
  }

  @CheckReturnValue
  @Nullable
  @Override
  public DownloadsFolder browserDownloadsFolder() {
    return browserDownloadsFolder;
  }

  void createDriver() {
    CreateDriverCommand.Result result = createDriverCommand.createDriver(config, factory, userProvidedProxy, eventListeners, listeners);
    this.webDriver = result.webDriver;
    this.selenideProxyServer = result.selenideProxyServer;
    this.browserDownloadsFolder = result.browserDownloadsFolder;
    this.closed = false;
  }

  @Override
  public void close() {
    closeDriverCommand.close(config, webDriver, selenideProxyServer);
    webDriver = null;
    selenideProxyServer = null;
    browserDownloadsFolder = null;
    closed = true;
  }
}
