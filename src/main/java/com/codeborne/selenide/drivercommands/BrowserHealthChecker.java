package com.codeborne.selenide.drivercommands;

import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.util.logging.Logger;

import static java.util.logging.Level.FINE;

public class BrowserHealthChecker {
  private static final Logger log = Logger.getLogger(BrowserHealthChecker.class.getName());

  public boolean isBrowserStillOpen(WebDriver webDriver) {
    try {
      webDriver.getTitle();
      return true;
    }
    catch (UnreachableBrowserException e) {
      log.log(FINE, "Browser is unreachable", e);
      return false;
    }
    catch (NoSuchWindowException e) {
      log.log(FINE, "Browser window is not found", e);
      return false;
    }
    catch (NoSuchSessionException e) {
      log.log(FINE, "Browser session is not found", e);
      return false;
    }
  }
}
