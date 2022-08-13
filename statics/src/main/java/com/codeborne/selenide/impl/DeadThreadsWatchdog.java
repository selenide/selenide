package com.codeborne.selenide.impl;

import com.codeborne.selenide.drivercommands.WebdriversRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

class DeadThreadsWatchdog extends Thread {
  private static final Logger log = LoggerFactory.getLogger(DeadThreadsWatchdog.class);

  private final Collection<Thread> allWebDriverThreads;
  private final Map<Long, WebDriverInstance> driverPerThread;

  DeadThreadsWatchdog(Collection<Thread> allWebDriverThreads,
                      Map<Long, WebDriverInstance> driverPerThread) {
    this.allWebDriverThreads = allWebDriverThreads;
    this.driverPerThread = driverPerThread;
    setDaemon(true);
    setName("Dead threads watchdog");
  }

  @Override
  public void run() {
    while (true) {
      closeUnusedWebdrivers();
      try {
        Thread.sleep(50);
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
        closeWebDriver(thread);
      }
    }
  }

  private void closeWebDriver(Thread thread) {
    allWebDriverThreads.remove(thread);
    WebDriverInstance driver = driverPerThread.remove(thread.getId());

    if (driver == null) {
      log.info("No webdriver found for thread: {} - nothing to close", thread.getId());
      return;
    }

    log.info("Thread {} is dead. Let's close its webdriver {}.", thread.getId(), driver.webDriver());
    driver.dispose();
    WebdriversRegistry.unregister(driver);
  }
}

