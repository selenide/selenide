package com.codeborne.selenide.impl;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

class UnusedWebdriversCleanupThread extends Thread {
  private static final Logger log = LoggerFactory.getLogger(UnusedWebdriversCleanupThread.class);

  private final Collection<Thread> allWebDriverThreads;
  private final Map<Long, WebDriver> threadWebDriver;
  private final Map<Long, SelenideProxyServer> threadProxyServer;
  private final Map<Long, DownloadsFolder> threadDownloadsFolder;

  UnusedWebdriversCleanupThread(Collection<Thread> allWebDriverThreads,
                                Map<Long, WebDriver> threadWebDriver,
                                Map<Long, SelenideProxyServer> threadProxyServer,
                                Map<Long, DownloadsFolder> threadDownloadsFolder) {
    this.allWebDriverThreads = allWebDriverThreads;
    this.threadWebDriver = threadWebDriver;
    this.threadProxyServer = threadProxyServer;
    this.threadDownloadsFolder = threadDownloadsFolder;
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
        log.info("Thread {} is dead. Let's close webdriver {}", thread.getId(), threadWebDriver.get(thread.getId()));
        closeWebDriver(thread);
      }
    }
  }

  private void closeWebDriver(Thread thread) {
    allWebDriverThreads.remove(thread);
    WebDriver driver = threadWebDriver.remove(thread.getId());

    if (driver == null) {
      log.info("No webdriver found for thread: {} - nothing to close", thread.getId());
    }
    else {
      driver.quit();
    }

    SelenideProxyServer proxy = threadProxyServer.remove(thread.getId());
    if (proxy != null) {
      proxy.shutdown();
    }

    threadDownloadsFolder.remove(thread.getId());
  }
}

