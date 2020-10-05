package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.Config;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.lang.System.currentTimeMillis;

@ParametersAreNonnullByDefault
public class CloseDriverCommand {
  private static final Logger log = LoggerFactory.getLogger(CloseDriverCommand.class);

  public void close(Config config, @Nullable WebDriver webDriver, @Nullable SelenideProxyServer selenideProxyServer) {
    long threadId = Thread.currentThread().getId();
    if (config.holdBrowserOpen()) {
      log.info("Hold browser and proxy open: {} -> {}, {}", threadId, webDriver, selenideProxyServer);
      return;
    }

    if (webDriver != null) {
      long start = currentTimeMillis();
      log.info("Close webdriver: {} -> {}...", threadId, webDriver);
      close(webDriver);
      log.info("Closed webdriver {} in {} ms", threadId, currentTimeMillis() - start);
    }

    if (selenideProxyServer != null) {
      long start = currentTimeMillis();
      log.info("Close proxy server: {} -> {}...", threadId, selenideProxyServer);
      selenideProxyServer.shutdown();
      log.info("Closed proxy server {} in {} ms", threadId, currentTimeMillis() - start);
    }
  }

  private void close(WebDriver webdriver) {
    try {
      webdriver.quit();
    }
    catch (UnreachableBrowserException e) {
      // It happens for Firefox. It's ok: browser is already closed.
      log.debug("Browser is unreachable", e);
    }
    catch (WebDriverException cannotCloseBrowser) {
      log.error("Cannot close browser: {}", Cleanup.of.webdriverExceptionMessage(cannotCloseBrowser));
    }
    catch (RuntimeException e) {
      log.error("Cannot close browser", e);
    }
  }
}
