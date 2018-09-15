package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.Config.BrowserConfig;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.holdBrowserOpen;
import static com.codeborne.selenide.Configuration.reopenBrowserOnFail;
import static java.lang.Thread.currentThread;

public class LazyDriver implements Driver {
  private static final Logger log = Logger.getLogger(LazyDriver.class.getName());

  private final BrowserConfig config;
  private final BrowserHealthChecker browserHealthChecker;
  private final WebDriverFactory factory;
  private final Proxy userProvidedProxy;
  private final List<WebDriverEventListener> listeners = new ArrayList<>();
  private final Browser browser = new Browser(Configuration.browser, Configuration.headless);

  private WebDriver webDriver;
  private SelenideProxyServer selenideProxyServer;

  public LazyDriver(BrowserConfig config, Proxy userProvidedProxy, List<WebDriverEventListener> listeners) {
    this(config, userProvidedProxy, listeners, new WebDriverFactory(), new BrowserHealthChecker());
  }

  LazyDriver(BrowserConfig config, Proxy userProvidedProxy, List<WebDriverEventListener> listeners,
             WebDriverFactory factory, BrowserHealthChecker browserHealthChecker) {
    this.config = config;
    this.userProvidedProxy = userProvidedProxy;
    this.listeners.addAll(listeners);
    this.factory = factory;
    this.browserHealthChecker = browserHealthChecker;
  }

  @Override
  public Browser browser() {
    return browser;
  }

  @Override
  public WebDriver getWebDriver() {
    return webDriver;
  }

  @Override
  public SelenideProxyServer getProxy() {
    return selenideProxyServer;
  }

  @Override
  public synchronized WebDriver getAndCheckWebDriver() {
    if (webDriver != null && reopenBrowserOnFail && !browserHealthChecker.isBrowserStillOpen(webDriver)) {
      log.info("Webdriver has been closed meanwhile. Let's re-create it.");
      close();
      createDriver();
    }
    else if (webDriver == null) {
      log.info("No webdriver is bound to current thread: " + currentThread().getId() + " - let's create a new webdriver");
      createDriver();
    }
    return getWebDriver();
  }

  void createDriver() {
    CreateDriverCommand.Result result = new CreateDriverCommand().createDriver(config, factory, userProvidedProxy, listeners);
    this.webDriver = result.webDriver;
    this.selenideProxyServer = result.selenideProxyServer;
    Runtime.getRuntime().addShutdownHook(new SelenideDriverFinalCleanupThread(this));
  }

  @Override
  public void close() {
    if (!holdBrowserOpen) {
      new CloseDriverCommand(webDriver, selenideProxyServer).run();
      webDriver = null;
      selenideProxyServer = null;
    }
  }
}
