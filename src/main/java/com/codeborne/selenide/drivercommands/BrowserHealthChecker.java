package com.codeborne.selenide.drivercommands;

import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BrowserHealthChecker {
  private static final Logger log = LoggerFactory.getLogger(BrowserHealthChecker.class);

  public boolean isBrowserStillOpen(WebDriver webDriver) {
    try {
      webDriver.getTitle();
      return true;
    }
    catch (UnreachableBrowserException e) {
      log.debug("Browser is unreachable", e);
      return false;
    }
    catch (NoSuchWindowException e) {
      log.debug("Browser window is not found", e);
      return false;
    }
    catch (NoSuchSessionException e) {
      log.debug("Browser session is not found", e);
      return false;
    }
  }
}
