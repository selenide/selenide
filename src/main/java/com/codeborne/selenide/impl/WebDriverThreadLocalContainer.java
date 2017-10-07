package com.codeborne.selenide.impl;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.FileDownloadMode.PROXY;
import static com.codeborne.selenide.Configuration.*;
import static com.codeborne.selenide.impl.Describe.describe;
import static java.lang.Thread.currentThread;
import static java.util.logging.Level.FINE;

public class WebDriverThreadLocalContainer implements WebDriverContainer {
  private static final Logger log = Logger.getLogger(WebDriverThreadLocalContainer.class.getName());

  protected WebDriverFactory factory = new WebDriverFactory();

  protected List<WebDriverEventListener> listeners = new ArrayList<>();
  protected Collection<Thread> ALL_WEB_DRIVERS_THREADS = new ConcurrentLinkedQueue<>();
  protected Map<Long, WebDriver> THREAD_WEB_DRIVER = new ConcurrentHashMap<>(4);
  protected Map<Long, SelenideProxyServer> THREAD_PROXY_SERVER = new ConcurrentHashMap<>(4);
  protected Proxy proxy;

  protected final AtomicBoolean cleanupThreadStarted = new AtomicBoolean(false);

  protected void closeUnusedWebdrivers() {
    for (Thread thread : ALL_WEB_DRIVERS_THREADS) {
      if (!thread.isAlive()) {
        log.info("Thread " + thread.getId() + " is dead. Let's close webdriver " + THREAD_WEB_DRIVER.get(thread.getId()));
        closeWebDriver(thread);
      }
    }
  }

  @Override
  public void addListener(WebDriverEventListener listener) {
    listeners.add(listener);
  }

  @Override
  public WebDriver setWebDriver(WebDriver webDriver) {
    THREAD_WEB_DRIVER.put(currentThread().getId(), webDriver);
    return webDriver;
  }

  @Override
  public void setProxy(Proxy webProxy) {
    proxy = webProxy;
  }

  protected boolean isBrowserStillOpen(WebDriver webDriver) {
    try {
      webDriver.getTitle();
      return true;
    } catch (UnreachableBrowserException e) {
      log.log(FINE, "Browser is unreachable", e);
      return false;
    } catch (NoSuchWindowException e) {
      log.log(FINE, "Browser window is not found", e);
      return false;
    } catch (NoSuchSessionException e) {
      log.log(FINE, "Browser session is not found", e);
      return false;
    }
  }

  /**
   * @return true iff webdriver is started in current thread
   */
  @Override
  public boolean hasWebDriverStarted() {
    return THREAD_WEB_DRIVER.containsKey(currentThread().getId());
  }

  @Override
  public WebDriver getWebDriver() {
    WebDriver webDriver = THREAD_WEB_DRIVER.get(currentThread().getId());
    if (webDriver != null) {
      return webDriver;
    }

    log.info("No webdriver is bound to current thread: " + currentThread().getId() + " - let's create new webdriver");
    return setWebDriver(createDriver());
  }

  @Override
  public WebDriver getAndCheckWebDriver() {
    WebDriver webDriver = THREAD_WEB_DRIVER.get(currentThread().getId());
    if (webDriver != null) {
      if (!reopenBrowserOnFail || isBrowserStillOpen(webDriver)) {
        return webDriver;
      }
      else {
        log.info("Webdriver has been closed meanwhile. Let's re-create it.");
        closeWebDriver();
      }
    }
    return setWebDriver(createDriver());
  }

  @Override
  public SelenideProxyServer getProxyServer() {
    return THREAD_PROXY_SERVER.get(currentThread().getId());
  }

  @Override
  public void closeWebDriver() {
    closeWebDriver(currentThread());
  }

  protected void closeWebDriver(Thread thread) {
    ALL_WEB_DRIVERS_THREADS.remove(thread);
    WebDriver webdriver = THREAD_WEB_DRIVER.remove(thread.getId());
    SelenideProxyServer proxy = THREAD_PROXY_SERVER.remove(thread.getId());

    if (webdriver != null && !holdBrowserOpen) {
      log.info("Close webdriver: " + thread.getId() + " -> " + webdriver);
      if (proxy != null) {
        log.info("Close proxy server: " + thread.getId() + " -> " + proxy);
      }

      long start = System.currentTimeMillis();

      Thread t = new Thread(new CloseBrowser(webdriver, proxy));
      t.setDaemon(true);
      t.start();

      try {
        t.join(closeBrowserTimeoutMs);
      } catch (InterruptedException e) {
        log.log(FINE, "Failed to close webdriver in " + closeBrowserTimeoutMs + " milliseconds", e);
      }

      long duration = System.currentTimeMillis() - start;
      if (duration >= closeBrowserTimeoutMs) {
        log.severe("Failed to close webdriver in " + closeBrowserTimeoutMs + " milliseconds");
      }
      else if (duration > 200) {
        log.info("Closed webdriver in " + duration + " ms");
      }
      else {
        log.fine("Closed webdriver in " + duration + " ms");
      }
    }
    else if (proxy != null && !holdBrowserOpen) {
      log.info("Close proxy server: " + thread.getId() + " -> " + proxy);
      proxy.shutdown();
    }
  }

  private static class CloseBrowser implements Runnable {
    private final WebDriver webdriver;
    private final SelenideProxyServer proxy;

    private CloseBrowser(WebDriver webdriver, SelenideProxyServer proxy) {
      this.webdriver = webdriver;
      this.proxy = proxy;
    }

    @Override
    public void run() {
      try {
        log.info("Trying to close the browser " + describe(webdriver) + " ...");
        webdriver.quit();
      }
      catch (UnreachableBrowserException e) {
        // It happens for Firefox. It's ok: browser is already closed.
        log.log(FINE, "Browser is unreachable", e);
      }
      catch (WebDriverException cannotCloseBrowser) {
        log.severe("Cannot close browser normally: " + Cleanup.of.webdriverExceptionMessage(cannotCloseBrowser));
      }

      if (proxy != null) {
        log.info("Trying to shutdown " + proxy + " ...");
        proxy.shutdown();
      }
    }
  }

  @Override
  public void clearBrowserCache() {
    WebDriver webdriver = THREAD_WEB_DRIVER.get(currentThread().getId());
    if (webdriver != null) {
      webdriver.manage().deleteAllCookies();
    }
  }

  @Override
  public String getPageSource() {
    return getWebDriver().getPageSource();
  }

  @Override
  public String getCurrentUrl() {
    return getWebDriver().getCurrentUrl();
  }

  @Override
  public String getCurrentFrameUrl() {
    return ((JavascriptExecutor) getWebDriver()).executeScript("return window.location.href").toString();
  }

  protected WebDriver createDriver() {
    Proxy userProvidedProxy = proxy;

    if (Configuration.fileDownload == PROXY) {
      SelenideProxyServer selenideProxyServer = new SelenideProxyServer(proxy);
      selenideProxyServer.start();
      THREAD_PROXY_SERVER.put(currentThread().getId(), selenideProxyServer);
      userProvidedProxy = selenideProxyServer.createSeleniumProxy();
    }

    WebDriver webdriver = factory.createWebDriver(userProvidedProxy);

    log.info("Create webdriver in current thread " + currentThread().getId() + ": " +
            describe(webdriver) + " -> " + webdriver);

    return markForAutoClose(addListeners(webdriver));
  }

  protected WebDriver addListeners(WebDriver webdriver) {
    if (listeners.isEmpty()) {
      return webdriver;
    }

    EventFiringWebDriver wrapper = new EventFiringWebDriver(webdriver);
    for (WebDriverEventListener listener : listeners) {
      log.info("Add listener to webdriver: " + listener);
      wrapper.register(listener);
    }
    return wrapper;
  }

  protected WebDriver markForAutoClose(WebDriver webDriver) {
    ALL_WEB_DRIVERS_THREADS.add(currentThread());

    if (!cleanupThreadStarted.get()) {
      synchronized (this) {
        if (!cleanupThreadStarted.get()) {
          new UnusedWebdriversCleanupThread().start();
          cleanupThreadStarted.set(true);
        }
      }
    }
    Runtime.getRuntime().addShutdownHook(new WebdriversFinalCleanupThread(currentThread()));
    return webDriver;
  }

  protected class WebdriversFinalCleanupThread extends Thread {
    private final Thread thread;

    public WebdriversFinalCleanupThread(Thread thread) {
      this.thread = thread;
    }

    @Override
    public void run() {
      closeWebDriver(thread);
    }
  }

  protected class UnusedWebdriversCleanupThread extends Thread {
    public UnusedWebdriversCleanupThread() {
      setDaemon(true);
      setName("Webdrivers killer thread");
    }

    @Override
    public void run() {
      while (true) {
        closeUnusedWebdrivers();
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    }
  }
}
