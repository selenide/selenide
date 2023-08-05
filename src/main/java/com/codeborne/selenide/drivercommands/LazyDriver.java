package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.WebDriverInstance;
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
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.currentThread;

/**
 * A `Driver` implementation which opens browser on demand (on a first call).
 * May be created with its own config, proxy and listeners.
 *
 * This class is NOT thread-safe. Every thread should use its own instance.
 */
@ParametersAreNonnullByDefault
@NotThreadSafe
@SuppressWarnings("deprecation")
public class LazyDriver implements Driver {
  private static final Logger log = LoggerFactory.getLogger(LazyDriver.class);

  private final Config config;
  private final BrowserHealthChecker browserHealthChecker;
  private final WebDriverFactory factory;
  private final CreateDriverCommand createDriverCommand;
  private final Proxy userProvidedProxy;
  private final List<WebDriverEventListener> eventListeners;
  private final List<WebDriverListener> listeners;
  private final Browser browser;

  private boolean closed;

  @Nullable
  private WebDriverInstance wd;

  public LazyDriver(Config config, @Nullable Proxy userProvidedProxy,
                    List<WebDriverEventListener> eventListeners, List<WebDriverListener> listeners) {
    this(config, userProvidedProxy, eventListeners, listeners, new WebDriverFactory(), new BrowserHealthChecker(),
      new CreateDriverCommand());
  }

  LazyDriver(Config config, @Nullable Proxy userProvidedProxy,
             List<WebDriverEventListener> eventListeners, List<WebDriverListener> listeners,
             WebDriverFactory factory, BrowserHealthChecker browserHealthChecker,
             CreateDriverCommand createDriverCommand) {
    this.config = config;
    this.browser = new Browser(config.browser(), config.headless());
    this.userProvidedProxy = userProvidedProxy;
    this.eventListeners = new ArrayList<>(eventListeners);
    this.listeners = new ArrayList<>(listeners);
    this.factory = factory;
    this.browserHealthChecker = browserHealthChecker;
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
    return wd != null && wd.webDriver() != null;
  }

  @Override
  @Nonnull
  public WebDriver getWebDriver() {
    return checkDriverIsStarted().webDriver();
  }

  @Nonnull
  @Override
  public SelenideProxyServer getProxy() {
    return checkDriverIsStarted().proxy();
  }

  @Nonnull
  private WebDriverInstance checkDriverIsStarted() {
    if (closed) {
      throw new IllegalStateException("Webdriver has been closed. You need to call open(url) to open a browser again.");
    }
    if (wd == null || wd.webDriver() == null) {
      throw new IllegalStateException("No webdriver is bound to current thread: " + currentThread().getId() +
                                      ". You need to call open(url) first.");
    }
    return wd;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver getAndCheckWebDriver() {
    if (wd != null && wd.webDriver() != null && !browserHealthChecker.isBrowserStillOpen(wd.webDriver())) {
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
    else if (wd == null || wd.webDriver() == null) {
      log.info("No webdriver is bound to current thread: {} - let's create a new webdriver", currentThread().getId());
      createDriver();
    }
    return getWebDriver();
  }

  @CheckReturnValue
  @Nullable
  @Override
  public DownloadsFolder browserDownloadsFolder() {
    return wd == null ? null : wd.downloadsFolder();
  }

  void createDriver() {
    this.wd = createDriverCommand.createDriver(config, factory, userProvidedProxy, eventListeners, listeners);
    this.closed = false;
  }

  @Override
  public void close() {
    if (wd != null) {
      wd.dispose();
      WebdriversRegistry.unregister(wd);
    }
    wd = null;
    closed = true;
  }
}
