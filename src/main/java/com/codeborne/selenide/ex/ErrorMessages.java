package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class ErrorMessages {
  protected static String timeout(long timeoutMs) {
    if (timeoutMs < 1000) {
      return "\nTimeout: " + timeoutMs + " ms.";
    }
    if (timeoutMs % 1000 == 0) {
      return "\nTimeout: " + timeoutMs / 1000 + " s.";
    }

    return "\nTimeout: " + String.format("%.3f", timeoutMs / 1000.0) + " s.";
  }

  static String actualValue(Condition condition, Driver driver, WebElement element) {
    if (element != null) {
      String actualValue = condition.actualValue(driver, element);
      if (actualValue != null) {
        return "\nActual value: " + actualValue;
      }
    }
    return "";
  }

  public static String screenshot(Driver driver) {
    return screenshot(driver.config(), ScreenShotLaboratory.getInstance().formatScreenShotPath(driver));
  }
  
  public static String screenshot(Config config, String screenshotPath) {
    if (!config.screenshots()) {
      return "";
    }

    if (screenshotPath == null || screenshotPath.isEmpty()) {
      return "\nScreenshot: " + screenshotPath;
    }

    if (config.savePageSource() && !screenshotPath.endsWith(".html")) {
      String htmlFilePath = getHtmlFilePath(screenshotPath);
      return "\nScreenshot: " + screenshotPath + "\nPage source: " + htmlFilePath;
    }
    else if (screenshotPath.endsWith(".html")) {
      return "\nPage source: " + screenshotPath;
    }
    else {
      return "\nScreenshot: " + screenshotPath;
    }
  }

  static String causedBy(Throwable cause) {
    if (cause == null) {
      return "";
    }
    if (cause instanceof WebDriverException) {
      return "\nCaused by: " + Cleanup.of.webdriverExceptionMessage(cause);
    }
    return "\nCaused by: " + cause;
  }

  private static String getHtmlFilePath(String screenshotPath) {
    return screenshotPath.substring(0, screenshotPath.lastIndexOf('.')) + ".html";
  }
}
