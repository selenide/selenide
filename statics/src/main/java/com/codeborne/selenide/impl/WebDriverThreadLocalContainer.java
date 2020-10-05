package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.SharedDownloadsFolder;
import com.codeborne.selenide.drivercommands.BrowserHealthChecker;
import com.codeborne.selenide.drivercommands.CloseDriverCommand;
import com.codeborne.selenide.drivercommands.CreateDriverCommand;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.codeborne.selenide.Configuration.reopenBrowserOnFail;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static java.lang.Thread.currentThread;

@ParametersAreNonnullByDefault
public class WebDriverThreadLocalContainer implements WebDriverContainer {
  private static final Logger log = LoggerFactory.getLogger(WebDriverThreadLocalContainer.class);

  private final List<WebDriverEventListener> listeners = new ArrayList<>();
  final Collection<Thread> allWebDriverThreads = new ConcurrentLinkedQueue<>();
  final Map<Long, WebDriver> threadWebDriver = new ConcurrentHashMap<>(4);
  private final Map<Long, SelenideProxyServer> threadProxyServer = new ConcurrentHashMap<>(4);
  private final Map<Long, DownloadsFolder> threadDownloadsFolder = new ConcurrentHashMap<>(4);
  @Nullable private Proxy userProvidedProxy;

  private final Config config = new StaticConfig();
  private final BrowserHealthChecker browserHealthChecker = new BrowserHealthChecker();
  private final WebDriverFactory factory = new WebDriverFactory();
  private final CloseDriverCommand closeDriverCommand = new CloseDriverCommand();
  private final CreateDriverCommand createDriverCommand = new CreateDriverCommand();

  final AtomicBoolean cleanupThreadStarted = new AtomicBoolean(false);

  @Override
  public void addListener(WebDriverEventListener listener) {
    listeners.add(listener);
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
    if (selenideProxy != null) {
      threadProxyServer.put(threadId, selenideProxy);
    }
    threadWebDriver.put(threadId, webDriver);
    threadDownloadsFolder.put(threadId, browserDownloadsFolder);
  }

  /**
   * Remove links to webdriver/proxy, but don't close the webdriver/proxy itself.
   */
  @Override
  public void resetWebDriver() {
    long threadId = currentThread().getId();
    threadProxyServer.remove(threadId);
    threadWebDriver.remove(threadId);
    threadDownloadsFolder.remove(threadId);
  }

  @Override
  public void setProxy(Proxy userProvidedProxy) {
    this.userProvidedProxy = userProvidedProxy;
  }

  /**
   * @return true iff webdriver is started in current thread
   */
  @Override
  @CheckReturnValue
  public boolean hasWebDriverStarted() {
    WebDriver webDriver = threadWebDriver.get(currentThread().getId());
    return webDriver != null;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver getWebDriver() {
    long threadId = currentThread().getId();
    if (!threadWebDriver.containsKey(threadId)) {
      throw new IllegalStateException("No webdriver is bound to current thread: " + threadId + ". You need to call open(url) first.");
    }
    return threadWebDriver.get(threadId);
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public WebDriver getAndCheckWebDriver() {
    WebDriver webDriver = threadWebDriver.get(currentThread().getId());

    if (webDriver != null && reopenBrowserOnFail && !browserHealthChecker.isBrowserStillOpen(webDriver)) {
      log.info("Webdriver has been closed meanwhile. Let's re-create it.");
      closeWebDriver();
      webDriver = createDriver();
    }
    else if (webDriver == null) {
      log.info("No webdriver is bound to current thread: {} - let's create a new webdriver", currentThread().getId());
      webDriver = createDriver();
    }
    return webDriver;
  }

  @Nonnull
  @Override
  public DownloadsFolder getBrowserDownloadsFolder() {
    return threadDownloadsFolder.get(currentThread().getId());
  }

  @CheckReturnValue
  @Nonnull
  private WebDriver createDriver() {
    CreateDriverCommand.Result result = createDriverCommand.createDriver(config, factory, userProvidedProxy, listeners);
    long threadId = currentThread().getId();
    threadWebDriver.put(threadId, result.webDriver);
    if (result.selenideProxyServer != null) {
      threadProxyServer.put(threadId, result.selenideProxyServer);
    }
    threadDownloadsFolder.put(threadId, result.browserDownloadsFolder);
    if (config.holdBrowserOpen()) {
      log.info("Browser and proxy will stay open due to holdBrowserOpen=true: {} -> {}, {}",
        threadId, result.webDriver, result.selenideProxyServer);
    }
    else {
      markForAutoClose(currentThread());
    }
    return result.webDriver;
  }

  @Override
  @CheckReturnValue
  @Nullable
  public SelenideProxyServer getProxyServer() {
    return threadProxyServer.get(currentThread().getId());
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
    WebDriver driver = threadWebDriver.get(threadId);
    SelenideProxyServer proxy = threadProxyServer.get(threadId);
    closeDriverCommand.close(config, driver, proxy);

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

  private void markForAutoClose(Thread thread) {
    allWebDriverThreads.add(thread);

    if (!cleanupThreadStarted.get()) {
      synchronized (this) {
        if (!cleanupThreadStarted.get()) {
          new UnusedWebdriversCleanupThread(allWebDriverThreads, threadWebDriver, threadProxyServer, threadDownloadsFolder).start();
          cleanupThreadStarted.set(true);
        }
      }
    }
  }
}
