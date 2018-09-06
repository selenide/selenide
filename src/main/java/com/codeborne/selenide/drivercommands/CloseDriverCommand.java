package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;

import java.util.logging.Logger;

import static com.codeborne.selenide.Configuration.closeBrowserTimeoutMs;
import static java.util.logging.Level.FINE;

public class CloseDriverCommand {
  private static final Logger log = Logger.getLogger(CloseDriverCommand.class.getName());

  private final WebDriver webDriver;
  private final SelenideProxyServer selenideProxyServer;

  public CloseDriverCommand(WebDriver webDriver, SelenideProxyServer selenideProxyServer) {
    this.webDriver = webDriver;
    this.selenideProxyServer = selenideProxyServer;
  }

  public void run() {
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
  }
}
