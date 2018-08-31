package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static java.lang.Thread.currentThread;

public class WebDriverThreadLocalContainer implements WebDriverContainer {
  private static final Logger log = Logger.getLogger(WebDriverThreadLocalContainer.class.getName());

  private final List<WebDriverEventListener> listeners = new ArrayList<>();
  private final Collection<Thread> allWebDriverThreads = new ConcurrentLinkedQueue<>();
  private final Map<Long, SelenideDriver> threadWebDriver = new ConcurrentHashMap<>(4);
  private Proxy userProvidedProxy;

  private final AtomicBoolean cleanupThreadStarted = new AtomicBoolean(false);

  @Override
  public void addListener(WebDriverEventListener listener) {
    listeners.add(listener);
  }

  @Override
  public void setWebDriver(WebDriver webDriver) {
    SelenideDriver previous = threadWebDriver.get(currentThread().getId());
    if (previous != null) {
      previous.close();
    }
    threadWebDriver.put(currentThread().getId(), new SelenideDriver(webDriver));
  }

  @Override
  public void setProxy(Proxy userProvidedProxy) {
    this.userProvidedProxy = userProvidedProxy;
  }

  /**
   * @return true iff webdriver is started in current thread
   */
  @Override
  public boolean hasWebDriverStarted() {
    return threadWebDriver.containsKey(currentThread().getId());
  }

  @Override
  public SelenideDriver getSelenideDriver() {
    return threadWebDriver.computeIfAbsent(currentThread().getId(),
      threadId -> markForAutoClose(currentThread(), new SelenideDriver(userProvidedProxy, listeners)));
  }

  @Override
  public WebDriver getWebDriver() {
    return getSelenideDriver().getWebDriver();
  }

  @Override
  public WebDriver getAndCheckWebDriver() {
    return getSelenideDriver().getAndCheckWebDriver();
  }

  @Override
  public SelenideProxyServer getProxyServer() {
    return getSelenideDriver().getProxyServer();
  }

  @Override
  public void closeWebDriver() {
    if (hasWebDriverStarted()) {
      getSelenideDriver().close();
    }
  }

  @Override
  public void clearBrowserCache() {
    if (hasWebDriverStarted()) {
      getSelenideDriver().clearCookies();
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

  private SelenideDriver markForAutoClose(Thread thread, SelenideDriver selenideDriver) {
    allWebDriverThreads.add(thread);

    if (!cleanupThreadStarted.get()) {
      synchronized (this) {
        if (!cleanupThreadStarted.get()) {
          new UnusedWebdriversCleanupThread(allWebDriverThreads, threadWebDriver).start();
          cleanupThreadStarted.set(true);
        }
      }
    }

    return selenideDriver;
  }
}
