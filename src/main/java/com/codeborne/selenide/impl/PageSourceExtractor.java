package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import org.openqa.selenium.Alert;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static java.nio.charset.StandardCharsets.UTF_8;

@ParametersAreNonnullByDefault
public class PageSourceExtractor {
  private static final Logger log = LoggerFactory.getLogger(PageSourceExtractor.class);
  protected Set<String> printedErrors = new ConcurrentSkipListSet<>();
  private final WebDriver driver;
  private final Config config;
  private final String fileName;

  public PageSourceExtractor(Config config, WebDriver driver, String fileName) {
    this.config = config;
    this.driver = driver;
    this.fileName = fileName;
  }

  @Nonnull
  protected File extract(boolean retryIfAlert) {
    File pageSource = new File(config.reportsFolder(), fileName + ".html");
    try {
      writeToFile(driver.getPageSource(), pageSource);
    } catch (UnhandledAlertException e) {
      if (retryIfAlert) {
        retryingExtractionOnAlert(e);
      } else {
        printOnce("savePageSourceToFile", e);
      }
    } catch (WebDriverException e) {
      log.warn("Failed to save page source to {}", fileName, e);
      writeToFile(e.toString(), pageSource);
      return pageSource;
    } catch (RuntimeException e) {
      log.error("Failed to save page source to {}", fileName, e);
      writeToFile(e.toString(), pageSource);
    }
    return pageSource;
  }

  protected void writeToFile(String content, File targetFile) {
    try (ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes(UTF_8))) {
      FileHelper.copyFile(in, targetFile);
    } catch (IOException e) {
      log.error("Failed to write file {}", targetFile.getAbsolutePath(), e);
    }
  }

  protected synchronized void printOnce(String action, Throwable error) {
    if (!printedErrors.contains(action)) {
      log.error(error.getMessage(), error);
      printedErrors.add(action);
    } else {
      log.error("Failed to {}: {}", action, error);
    }
  }

  private void retryingExtractionOnAlert(Exception e) {
    try {
      Alert alert = driver.switchTo().alert();
      log.error("{}: {}", e, alert.getText());
      alert.accept();
      extract(false);
    } catch (Exception unableToCloseAlert) {
      log.error("Failed to close alert", unableToCloseAlert);
    }
  }
}
