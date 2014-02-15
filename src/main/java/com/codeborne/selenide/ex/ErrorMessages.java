package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import static com.codeborne.selenide.Screenshots.takeScreenShot;

public class ErrorMessages {
  private static final Logger LOG = Logger.getLogger(ErrorMessages.class.getName());

  protected static String timeout(long timeoutMs) {
    if (timeoutMs < 1000) {
      return "\nTimeout: " + timeoutMs + " ms.";
    }
    if (timeoutMs % 1000 == 0) {
      return "\nTimeout: " + timeoutMs / 1000 + " s.";
    }

    return "\nTimeout: " + String.format("%.3f", timeoutMs/1000.0) + " s.";
  }

  public static String actualValue(Condition condition, WebElement element) {
    if (element != null) {
      String actualValue = condition.actualValue(element);
      if (actualValue != null) {
        return "\nActual value: " + actualValue;
      }
    }
    return "";
  }

  public static String screenshot() {
    if (!Configuration.screenshots) {
      LOG.fine("Automatic screenshots are disabled.");
      return "";
    }

    return formatScreenShotPath(takeScreenShot());
  }

  private static String formatScreenShotPath(String screenshot) {
    if (Configuration.reportsUrl != null) {
      String screenshotRelativePath = screenshot.substring(System.getProperty("user.dir").length() + 1);
      String screenshotUrl = Configuration.reportsUrl + screenshotRelativePath.replace('\\', '/');
      try {
        screenshotUrl = new URL(screenshotUrl).toExternalForm();
      }
      catch (MalformedURLException e) {}
      LOG.info("Replaced screenshot file path '" + screenshot + "' by public CI URL '" + screenshotUrl + "'");
      return "\nScreenshot: " + screenshotUrl;
    }

    LOG.info("reportsUrl is not configured. Returning screenshot file name '" + screenshot + "'");
    try {
      return "\nScreenshot: " + new File(screenshot).toURI().toURL().toExternalForm();
    } catch (MalformedURLException e) {
      return "\nScreenshot: file://" + screenshot;
    }
  }
}
