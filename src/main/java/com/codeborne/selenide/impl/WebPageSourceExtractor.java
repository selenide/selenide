package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.openqa.selenium.Alert;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WebPageSourceExtractor implements PageSourceExtractor {
  private static final Logger log = LoggerFactory.getLogger(WebPageSourceExtractor.class);
  private final Set<String> printedErrors = new ConcurrentSkipListSet<>();

  @Override
  public File extract(Config config, WebDriver driver, String fileName) {
    return extract(config, driver, fileName, true);
  }

  @CanIgnoreReturnValue
  private File extract(Config config, WebDriver driver, String fileName, boolean retryIfAlert) {
    File pageSource = createFile(config, driver, fileName);
    try {
      String source = driver.getPageSource();
      if (source == null) {
        log.error("Failed to save page source to {}: page source is <null>", fileName);
        writeToFile("<null>", pageSource);
      }
      else {
        writeToFile(source, pageSource);
      }
    }
    catch (UnhandledAlertException e) {
      if (retryIfAlert) {
        retryingExtractionOnAlert(config, driver, fileName, e);
      }
      else {
        printOnce("savePageSourceToFile", e);
      }
    }
    catch (WebDriverException e) {
      log.warn("Failed to save page source to {}", fileName, e);
      writeToFile(e.toString(), pageSource);
      return pageSource;
    }
    catch (RuntimeException e) {
      log.error("Failed to save page source to {}", fileName, e);
      writeToFile(e.toString(), pageSource);
    }
    return pageSource;
  }

  protected File createFile(Config config, WebDriver driver, String fileName) {
    return new File(config.reportsFolder(), fileName + ".html").getAbsoluteFile();
  }

  protected void writeToFile(String content, File targetFile) {
    try (ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes(UTF_8))) {
      FileHelper.copyFile(in, targetFile);
    }
    catch (IOException e) {
      log.error("Failed to write file {}", targetFile.getAbsolutePath(), e);
    }
  }

  protected synchronized void printOnce(String action, Throwable error) {
    if (!printedErrors.contains(action)) {
      log.error(error.getMessage(), error);
      printedErrors.add(action);
    }
    else {
      log.error("Failed to {}: {}", action, error.toString());
    }
  }

  private void retryingExtractionOnAlert(Config config, WebDriver driver, String fileName, Exception e) {
    try {
      Alert alert = driver.switchTo().alert();
      log.error("{}: {}", e, alert.getText());
      alert.accept();
      extract(config, driver, fileName, false);
    }
    catch (Exception unableToCloseAlert) {
      log.error("Failed to close alert", unableToCloseAlert);
    }
  }
}
