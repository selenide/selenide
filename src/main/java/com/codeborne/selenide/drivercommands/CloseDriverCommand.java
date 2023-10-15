package com.codeborne.selenide.drivercommands;

import com.codeborne.selenide.DownloadsFolder;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.WebDriverInstance;
import com.codeborne.selenide.proxy.SelenideProxyServer;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;

import static java.lang.System.currentTimeMillis;

@ParametersAreNonnullByDefault
public class CloseDriverCommand {
  private static final Logger log = LoggerFactory.getLogger(CloseDriverCommand.class);

  public void close(WebDriverInstance wd) {
    WebDriver webDriver = wd.webDriver();
    DownloadsFolder downloadsFolder = wd.downloadsFolder();
    long threadId = Thread.currentThread().getId();
    if (wd.config().holdBrowserOpen()) {
      log.info("Hold browser open: {} -> {}", threadId, webDriver);
      return;
    }

    if (downloadsFolder != null) {
      downloadsFolder.deleteIfEmpty();
    }

    if (webDriver != null) {
      long start = currentTimeMillis();
      log.info("Close webdriver: {} -> {}...", threadId, webDriver);
      quitSafely(webDriver);
      log.info("Closed webdriver {} in {} ms", threadId, currentTimeMillis() - start);
    }

    if (wd.config().proxyEnabled()) {
      long start = currentTimeMillis();
      SelenideProxyServer proxy = wd.proxy();
      log.info("Close proxy server: {} -> {}...", threadId, proxy);
      proxy.shutdown();
      log.info("Closed proxy server {} in {} ms", threadId, currentTimeMillis() - start);
    }
  }

  private void quitSafely(WebDriver webdriver) {
    try {
      webdriver.quit();
    }
    catch (NoSuchSessionException e) {
      log.debug("Webdriver has been closed meanwhile", e);
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
