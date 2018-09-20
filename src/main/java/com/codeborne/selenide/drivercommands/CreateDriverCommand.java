package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import com.codeborne.selenide.webdriver.WebDriverFactory;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.util.List;
import java.util.logging.Logger;

import static java.lang.Thread.currentThread;

public class CreateDriverCommand {
  private static final Logger log = Logger.getLogger(CreateDriverCommand.class.getName());

  public Result createDriver(Config config,
                             WebDriverFactory factory,
                             Proxy userProvidedProxy,
                             List<WebDriverEventListener> listeners) {
    if (!config.reopenBrowserOnFail()) {
      throw new IllegalStateException("No webdriver is bound to current thread: " + currentThread().getId() +
        ", and cannot create a new webdriver because reopenBrowserOnFail=false");
    }

    SelenideProxyServer selenideProxyServer = null;

    Proxy browserProxy = userProvidedProxy;

    if (config.proxyEnabled()) {
      selenideProxyServer = new SelenideProxyServer(config, userProvidedProxy);
      selenideProxyServer.start();
      browserProxy = selenideProxyServer.createSeleniumProxy();
    }

    WebDriver webdriver = factory.createWebDriver(config, browserProxy);

    log.info("Create webdriver in current thread " + currentThread().getId() + ": " +
      webdriver.getClass().getSimpleName() + " -> " + webdriver);

    WebDriver webDriver = addListeners(webdriver, listeners);
    return new Result(webDriver, selenideProxyServer);
  }

  private WebDriver addListeners(WebDriver webdriver, List<WebDriverEventListener> listeners) {
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

  public static class Result {
    public final WebDriver webDriver;
    public final SelenideProxyServer selenideProxyServer;

    public Result(WebDriver webDriver, SelenideProxyServer selenideProxyServer) {
      this.webDriver = webDriver;
      this.selenideProxyServer = selenideProxyServer;
    }
  }
}
