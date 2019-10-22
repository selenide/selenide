package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseDriverCommand {
  private static final Logger log = LoggerFactory.getLogger(CloseDriverCommand.class);

  public void run(WebDriver webDriver, SelenideProxyServer selenideProxyServer) {
    long threadId = Thread.currentThread().getId();
    if (webDriver != null) {
      log.info("Close webdriver: {} -> {}", threadId, webDriver);
      if (selenideProxyServer != null) {
        log.info("Close proxy server: {} -> {}", threadId, selenideProxyServer);
      }

      long start = System.currentTimeMillis();

      Thread t = new Thread(new CloseBrowser(webDriver, selenideProxyServer));
      t.setDaemon(true);
      t.start();

      try {
        t.join();
      } catch (InterruptedException e) {
        long duration = System.currentTimeMillis() - start;
        log.debug("Failed to close webdriver {} in {} ms", threadId, duration, e);
        Thread.currentThread().interrupt();
      }

      long duration = System.currentTimeMillis() - start;
      log.info("Closed webdriver {} in {} ms", threadId, duration);
    } else if (selenideProxyServer != null) {
      log.info("Close proxy server: {} -> {}", threadId, selenideProxyServer);
      selenideProxyServer.shutdown();
    }
  }
}
