package com.codeborne.selenide.impl;

import com.codeborne.selenide.Config;
import org.openqa.selenium.Alert;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

class PageSourceExtractor {
  private static final Logger log = LoggerFactory.getLogger(PageSourceExtractor.class);
  private boolean errorPrinted;

  File extract(Config config, WebDriver driver, String fileName) {
    return extract(config, driver, fileName, true);
  }

  private File extract(Config config, WebDriver driver, String fileName, boolean retryIfAlert) {
    File pageSource = new File(config.reportsFolder(), fileName + ".html");
    try {
      writeToFile(driver.getPageSource(), pageSource);
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
      writeToFile(e.toString(), pageSource);
      return pageSource;
    }
    catch (RuntimeException e) {
      log.error("Failed to save page source to {}", fileName, e);
      writeToFile(e.toString(), pageSource);
    }
    return pageSource;
  }


  protected void writeToFile(String content, File targetFile) {
    try (ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes(UTF_8))) {
      copyFile(in, targetFile);
    }
    catch (IOException e) {
      log.error("Failed to write file {}", targetFile.getAbsolutePath(), e);
    }
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

  protected void copyFile(InputStream in, File targetFile) throws IOException {
    ensureFolderExists(targetFile);

    try (FileOutputStream out = new FileOutputStream(targetFile)) {
      byte[] buffer = new byte[1024];
      int len;
      while ((len = in.read(buffer)) != -1) {
        out.write(buffer, 0, len);
      }
    }
  }

  protected void ensureFolderExists(File targetFile) {
    File folder = targetFile.getParentFile();
    if (!folder.exists()) {
      log.info("Creating folder: {}", folder);
      if (!folder.mkdirs()) {
        log.error("Failed to create {}", folder);
      }
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
