package com.codeborne.selenide;

import com.codeborne.selenide.impl.BrowserHealthChecker;
import com.codeborne.selenide.impl.CloseBrowser;
import com.codeborne.selenide.impl.Navigator;
import com.codeborne.selenide.impl.SelenideDriverFinalCleanupThread;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.closeBrowserTimeoutMs;
import static com.codeborne.selenide.Configuration.holdBrowserOpen;
import static com.codeborne.selenide.Configuration.reopenBrowserOnFail;
import static java.lang.Thread.currentThread;
import static java.util.Collections.emptyList;
import static java.util.logging.Level.FINE;

public class SelenideDriver implements Context {
  private static final Logger log = Logger.getLogger(SelenideDriver.class.getName());

  private final Navigator navigator = new Navigator();
  private final WebDriverFactory factory;
  private final BrowserHealthChecker browserHealthChecker;

  private final Browser browser = new Browser(Configuration.browser, Configuration.headless);

  // TODO split to 2 different classes:

  // Class 1:
  private final Proxy userProvidedProxy;
  private final List<WebDriverEventListener> listeners = new ArrayList<>();
  private SelenideProxyServer selenideProxyServer;

  // Class 2:
  private WebDriver webDriver;

  SelenideDriver(Proxy userProvidedProxy, List<WebDriverEventListener> listeners,
                 WebDriverFactory factory, BrowserHealthChecker browserHealthChecker) {
    this.userProvidedProxy = userProvidedProxy;
    this.listeners.addAll(listeners);
    this.factory = factory;
    this.browserHealthChecker = browserHealthChecker;
    Runtime.getRuntime().addShutdownHook(new SelenideDriverFinalCleanupThread(this));
  }

  public SelenideDriver() {
    this(null, emptyList(), new WebDriverFactory(), new BrowserHealthChecker());
  }

  public SelenideDriver(Proxy userProvidedProxy, List<WebDriverEventListener> listeners, WebDriverFactory factory) {
    this(userProvidedProxy, listeners, factory, new BrowserHealthChecker());
  }

  public SelenideDriver(WebDriver webDriver, WebDriverFactory factory) {
    this(webDriver, factory, new BrowserHealthChecker());
  }

  SelenideDriver(WebDriver webDriver, WebDriverFactory factory, BrowserHealthChecker browserHealthChecker) {
    this(null, emptyList(), factory, browserHealthChecker);
    this.webDriver = webDriver;
  }

  public void open(String relativeOrAbsoluteUrl) {
    navigator.open(this, relativeOrAbsoluteUrl);
  }

  public void open(URL absoluteUrl) {
    navigator.open(this, absoluteUrl);
  }

  public void open(String relativeOrAbsoluteUrl, String domain, String login, String password) {
    navigator.open(this, relativeOrAbsoluteUrl, domain, login, password);
  }

  public void open(String relativeOrAbsoluteUrl, AuthenticationType authenticationType, Credentials credentials) {
    navigator.open(this, relativeOrAbsoluteUrl, authenticationType, credentials);
  }

  public void open(URL absoluteUrl, String domain, String login, String password) {
    navigator.open(this, absoluteUrl, domain, login, password);
  }

  public Browser getBrowser() {
    return browser;
  }

  public boolean hasWebDriverStarted() {
    return webDriver != null;
  }

  public SelenideProxyServer getProxy() {
    return selenideProxyServer;
  }

  public synchronized WebDriver getWebDriver() {
    if (webDriver == null) {
      log.info("No webdriver is bound to current thread: " + currentThread().getId() + " - let's create a new webdriver");
      webDriver = createDriver();
    }
    return webDriver;
  }

  public WebDriver getAndCheckWebDriver() {
    if (webDriver != null && reopenBrowserOnFail && !browserHealthChecker.isBrowserStillOpen(webDriver)) {
      log.info("Webdriver has been closed meanwhile. Let's re-create it.");
      close();
    }
    return getWebDriver();
  }

  WebDriver createDriver() {
    if (!reopenBrowserOnFail) {
      throw new IllegalStateException("No webdriver is bound to current thread: " + currentThread().getId() +
        ", and cannot create a new webdriver because Configuration.reopenBrowserOnFail=false");
    }

    Proxy browserProxy = userProvidedProxy;

    if (Configuration.proxyEnabled) {
      selenideProxyServer = new SelenideProxyServer(userProvidedProxy);
      selenideProxyServer.start();
      browserProxy = selenideProxyServer.createSeleniumProxy();
    }

    WebDriver webdriver = factory.createWebDriver(browserProxy);

    log.info("Create webdriver in current thread " + currentThread().getId() + ": " +
      webdriver.getClass().getSimpleName() + " -> " + webdriver);

    return addListeners(webdriver);
  }

  private WebDriver addListeners(WebDriver webdriver) {
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

  public void clearCookies() {
    if (webDriver != null) {
      webDriver.manage().deleteAllCookies();
    }
  }

  public void close() {
    if (holdBrowserOpen) return;

    long threadId = Thread.currentThread().getId();
    if (webDriver != null) {
      log.info("Close webdriver: " + threadId + " -> " + webDriver);
      if (selenideProxyServer != null) {
        log.info("Close proxy server: " + threadId + " -> " + selenideProxyServer);
      }

      long start = System.currentTimeMillis();

      Thread t = new Thread(new CloseBrowser(webDriver, selenideProxyServer));
      t.setDaemon(true);
      t.start();

      try {
        t.join(closeBrowserTimeoutMs);
      }
      catch (InterruptedException e) {
        log.log(FINE, "Failed to close webdriver " + threadId + " in " + closeBrowserTimeoutMs + " milliseconds", e);
      }

      long duration = System.currentTimeMillis() - start;
      if (duration >= closeBrowserTimeoutMs) {
        log.severe("Failed to close webdriver " + threadId + " in " + closeBrowserTimeoutMs + " milliseconds");
      }
      else {
        log.info("Closed webdriver " + threadId + " in " + duration + " ms");
      }
    }
    else if (selenideProxyServer != null) {
      log.info("Close proxy server: " + threadId + " -> " + selenideProxyServer);
      selenideProxyServer.shutdown();
    }

    webDriver = null;
    selenideProxyServer = null;
  }

  public boolean supportsJavascript() {
    return hasWebDriverStarted() && getWebDriver() instanceof JavascriptExecutor;
  }

  @SuppressWarnings("unchecked")
  public <T> T executeJavaScript(String jsCode, Object... arguments) {
    return (T) ((JavascriptExecutor) getWebDriver()).executeScript(jsCode, arguments);
  }

  public WebElement getFocusedElement() {
    return (WebElement) executeJavaScript("return document.activeElement");
  }

  public SelenideTargetLocator switchTo() {
    return new SelenideTargetLocator(getWebDriver());
  }

  public Actions actions() {
    return new Actions(getWebDriver());
  }
}
