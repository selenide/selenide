package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.SharedDownloadsFolder;
import com.codeborne.selenide.drivercommands.BrowserHealthChecker;
import com.codeborne.selenide.drivercommands.CreateDriverCommand;
import com.codeborne.selenide.drivercommands.WebdriversRegistry;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static java.lang.Thread.currentThread;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
public class WebDriverThreadLocalContainer implements WebDriverContainer {
  private static final Logger log = LoggerFactory.getLogger(WebDriverThreadLocalContainer.class);

  private final List<WebDriverEventListener> eventListeners = new ArrayList<>();
  private final List<WebDriverListener> listeners = new ArrayList<>();
  final Collection<Thread> allWebDriverThreads = new ConcurrentLinkedQueue<>();
  final Map<Long, WebDriverInstance> threadWebDriver = new ConcurrentHashMap<>(4);

  @Nullable
  private Proxy userProvidedProxy;

  private final Config config = new StaticConfig();
  private final BrowserHealthChecker browserHealthChecker;
  private final WebDriverFactory factory = new WebDriverFactory();
  private final CreateDriverCommand createDriverCommand = new CreateDriverCommand();

  private final AtomicBoolean deadThreadsWatchdogStarted = new AtomicBoolean(false);

  public WebDriverThreadLocalContainer() {
    this(new BrowserHealthChecker());
  }

  WebDriverThreadLocalContainer(BrowserHealthChecker browserHealthChecker) {
    this.browserHealthChecker = browserHealthChecker;
  }

  @Override
  public void addListener(WebDriverEventListener listener) {
    eventListeners.add(listener);
  }

  @Override
  public void addListener(WebDriverListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeListener(WebDriverEventListener listener) {
    eventListeners.remove(listener);
  }

  @Override
  public void removeListener(WebDriverListener listener) {
    listeners.remove(listener);
  }

  @Override
  public void setWebDriver(WebDriver webDriver) {
    setWebDriver(webDriver, null);
  }

  @Override
  public void setWebDriver(WebDriver webDriver, @Nullable SelenideProxyServer selenideProxy) {
    setWebDriver(webDriver, selenideProxy, new SharedDownloadsFolder(config.downloadsFolder()));
  }

  /**
   * Make Selenide use given webdriver [and proxy] in the current thread.
   *
   * NB! This method is meant to be called BEFORE performing any actions with web elements.
   * It does NOT close a previously opened webdriver/proxy.
   *
   * @param webDriver any webdriver created by user
   * @param selenideProxy any proxy created by user (or null if proxy is not needed)
   * @param browserDownloadsFolder downloads folder - unique for the given browser instance
   */
  @Override
  public void setWebDriver(WebDriver webDriver, @Nullable SelenideProxyServer selenideProxy, DownloadsFolder browserDownloadsFolder) {
    resetWebDriver();

    long threadId = currentThread().getId();
    threadWebDriver.put(threadId, new WebDriverInstance(config, webDriver, selenideProxy, browserDownloadsFolder));
  }

  /**
   * Remove links to webdriver/proxy, but DON'T CLOSE the webdriver/proxy itself.
   */
  @Override
  public void resetWebDriver() {
    threadWebDriver.remove(currentThread().getId());
  }

  @Override
  public void setProxy(@Nullable Proxy userProvidedProxy) {
    this.userProvidedProxy = userProvidedProxy;
  }

  /**
   * @return true iff webdriver is started in current thread
   */
  @Override
  @CheckReturnValue
  public boolean hasWebDriverStarted() {
    return currentThreadDriver().map(driver -> driver.webDriver() != null).orElse(false);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver getWebDriver() {
    return currentThreadDriver().map(WebDriverInstance::webDriver)
      .orElseThrow(() -> new IllegalStateException(
        "No webdriver is bound to current thread: " + currentThread().getId() + ". You need to call open(url) first.")
      );
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver getAndCheckWebDriver() {
    WebDriver webDriver = currentThreadDriver().map(WebDriverInstance::webDriver).orElse(null);
    if (webDriver == null) {
      log.info("No webdriver is bound to current thread: {} - let's create a new webdriver", currentThread().getId());
      return createDriver().webDriver();
    }

    if (browserHealthChecker.isBrowserStillOpen(webDriver)) {
      return webDriver;
    }

    if (!config.reopenBrowserOnFail()) {
      closeWebDriver();
      throw new IllegalStateException("Webdriver for current thread: " + currentThread().getId() +
        " has been closed meanwhile, and cannot create a new webdriver because reopenBrowserOnFail=false");
    }

    log.info("Webdriver has been closed meanwhile. Let's re-create it.");
    closeWebDriver();
    return createDriver().webDriver();
  }

  @Nullable
  @Override
  public DownloadsFolder getBrowserDownloadsFolder() {
    return currentThreadDriver().map(WebDriverInstance::downloadsFolder).orElse(null);
  }

  private Optional<WebDriverInstance> currentThreadDriver() {
    return Optional.ofNullable(threadWebDriver.get(currentThread().getId()));
  }

  @CheckReturnValue
  @Nonnull
  private WebDriverInstance createDriver() {
    WebDriverInstance result = createDriverCommand.createDriver(config, factory, userProvidedProxy, eventListeners, listeners);
    long threadId = currentThread().getId();
    threadWebDriver.put(threadId, result);

    if (config.holdBrowserOpen()) {
      log.info("Browser and proxy will stay open due to holdBrowserOpen=true: {} -> {}, {}",
        threadId, result.webDriver(), result.proxy());
    }
    else {
      markForAutoClose(currentThread());
    }
    return result;
  }

  @Override
  @CheckReturnValue
  @Nullable
  public SelenideProxyServer getProxyServer() {
    return currentThreadDriver().map(WebDriverInstance::proxy).orElse(null);
  }

  @Override
  public void closeWindow() {
    getWebDriver().close();
  }

  /**
   * Remove links to webdriver/proxy AND close the webdriver and proxy
   */
  @Override
  public void closeWebDriver() {
    long threadId = currentThread().getId();
    WebDriverInstance driver = threadWebDriver.get(threadId);
    if (driver != null) {
      driver.dispose();
      WebdriversRegistry.unregister(driver);
    }

    resetWebDriver();
  }

  @Override
  public void clearBrowserCache() {
    if (hasWebDriverStarted()) {
      getWebDriver().manage().deleteAllCookies();
    }
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String getPageSource() {
    return getWebDriver().getPageSource();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String getCurrentUrl() {
    return getWebDriver().getCurrentUrl();
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String getCurrentFrameUrl() {
    //noinspection ConstantConditions
    return executeJavaScript("return window.location.href").toString();
  }

  boolean isDeadThreadsWatchdogStarted() {
    return deadThreadsWatchdogStarted.get();
  }

  private void markForAutoClose(Thread thread) {
    allWebDriverThreads.add(thread);

    if (!isDeadThreadsWatchdogStarted()) {
      synchronized (this) {
        if (!isDeadThreadsWatchdogStarted()) {
          new DeadThreadsWatchdog(allWebDriverThreads, threadWebDriver).start();
          deadThreadsWatchdogStarted.set(true);
        }
      }
    }
  }
}
