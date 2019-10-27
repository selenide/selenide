package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import org.openqa.selenium.Alert;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

class PageSourceExtractor {
  private static final Logger log = LoggerFactory.getLogger(PageSourceExtractor.class);
  private final IO io = new IO();
  private boolean errorPrinted;

  public File extract(Config config, WebDriver driver, String fileName) {
    return extract(config, driver, fileName, true);
  }

  private File extract(Config config, WebDriver driver, String fileName, boolean retryIfAlert) {
    File pageSource = new File(config.reportsFolder(), fileName + ".html");
    try {
      io.writeToFile(driver.getPageSource(), pageSource);
    }
    catch (UnhandledAlertException e) {
      if (retryIfAlert) {
        closeAlert(driver, e);
        extract(config, driver, fileName, false);
      }
      else {
        printOnce("savePageSourceToFile", e);
      }
    }
    catch (WebDriverException e) {
      log.warn("Failed to save page source to {}", fileName, e);
      io.writeToFile(e.toString(), pageSource);
      return pageSource;
    }
    catch (RuntimeException e) {
      log.error("Failed to save page source to {}", fileName, e);
      io.writeToFile(e.toString(), pageSource);
    }
    return pageSource;
  }

  protected synchronized void printOnce(String action, Throwable error) {
    if (!errorPrinted) {
      log.error(error.getMessage(), error);
      errorPrinted = true;
    }
    else {
      log.error("Failed to {}: {}", action, error);
    }
  }

  private void closeAlert(WebDriver driver, Exception e) {
    try {
      Alert alert = driver.switchTo().alert();
      log.error("Closing unexpected alert {}: {}", e, alert.getText());
      alert.accept();
    }
    catch (Exception unableToCloseAlert) {
      log.error("Failed to close alert", unableToCloseAlert);
    }
  }
}
