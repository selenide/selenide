package com.codeborne.selenide.impl;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

class UnusedWebdriversCleanupThread extends Thread {
  private static final Logger log = Logger.getLogger(UnusedWebdriversCleanupThread.class.getName());

  private final Collection<Thread> allWebDriverThreads;
  private final Map<Long, WebDriver> threadWebDriver;
  private final Map<Long, SelenideProxyServer> threadProxyServer;

  UnusedWebdriversCleanupThread(Collection<Thread> allWebDriverThreads, Map<Long, WebDriver> threadWebDriver,
                                Map<Long, SelenideProxyServer> threadProxyServer) {
    this.allWebDriverThreads = allWebDriverThreads;
    this.threadWebDriver = threadWebDriver;
    this.threadProxyServer = threadProxyServer;
    setDaemon(true);
    setName("Webdrivers killer thread");
  }

  @Override
  public void run() {
    while (true) {
      closeUnusedWebdrivers();
      try {
        Thread.sleep(100);
      }
      catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }

  private void closeUnusedWebdrivers() {
    for (Thread thread : allWebDriverThreads) {
      if (!thread.isAlive()) {
        log.info("Thread " + thread.getId() + " is dead. Let's close webdriver " + threadWebDriver.get(thread.getId()));
        closeWebDriver(thread);
      }
    }
  }

  private void closeWebDriver(Thread thread) {
    allWebDriverThreads.remove(thread);
    WebDriver driver = threadWebDriver.remove(thread.getId());

    if (driver == null) {
      log.info("No webdriver found for thread : " + thread.getId() + " - nothing to close");
    }
    else {
      driver.quit();
    }

    SelenideProxyServer proxy = threadProxyServer.remove(thread.getId());
    if (proxy != null) {
      proxy.shutdown();
    }
  }
}

