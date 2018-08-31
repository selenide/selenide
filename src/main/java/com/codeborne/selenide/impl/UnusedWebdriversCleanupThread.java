package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideDriver;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

class UnusedWebdriversCleanupThread extends Thread {
  private static final Logger log = Logger.getLogger(UnusedWebdriversCleanupThread.class.getName());

  private final Collection<Thread> allWebDriverThreads;
  private final Map<Long, SelenideDriver> threadWebDriver;

  UnusedWebdriversCleanupThread(Collection<Thread> allWebDriverThreads, Map<Long, SelenideDriver> threadWebDriver) {
    this.allWebDriverThreads = allWebDriverThreads;
    this.threadWebDriver = threadWebDriver;
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
    SelenideDriver selenideDriver = threadWebDriver.remove(thread.getId());

    if (selenideDriver == null) {
      log.info("No webdriver found for thread : " + thread.getId() + " - nothing to close");
    }
    else {
      selenideDriver.close();
    }
  }
}

