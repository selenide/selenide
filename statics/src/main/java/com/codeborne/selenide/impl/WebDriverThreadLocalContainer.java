package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.proxy.SelenideProxyServer;
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

import static java.lang.Thread.currentThread;

public class WebDriverThreadLocalContainer implements WebDriverContainer {
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
    setWebDriver(webDriver, null);
  }

  @Override
  public void setWebDriver(WebDriver webDriver, SelenideProxyServer selenideProxy) {
    SelenideDriver previous = threadWebDriver.get(currentThread().getId());
    if (previous != null) {
      previous.close();
    }
    threadWebDriver.put(currentThread().getId(), new SelenideDriver(new StaticConfig(), webDriver, selenideProxy));
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
    SelenideDriver selenideDriver = threadWebDriver.get(currentThread().getId());
    return selenideDriver != null && selenideDriver.hasWebDriverStarted();
  }

  @Override
  public SelenideDriver getSelenideDriver() {
    return threadWebDriver.computeIfAbsent(currentThread().getId(),
      threadId -> markForAutoClose(currentThread(), new SelenideDriver(new StaticConfig(), userProvidedProxy, listeners)));
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
    return getSelenideDriver().getProxy();
  }

  @Override
  public void closeWebDriver() {
    SelenideDriver driver = threadWebDriver.remove(currentThread().getId());
    if (driver != null) {
      driver.close();
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
    return getSelenideDriver().source();
  }

  @Override
  public String getCurrentUrl() {
    return getSelenideDriver().url();
  }

  @Override
  public String getCurrentFrameUrl() {
    return getSelenideDriver().getCurrentFrameUrl();
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
