package com.codeborne.selenide.ex;

import com.codeborne.selenide.Configuration;

import java.util.logging.Logger;

import static com.codeborne.selenide.WebDriverRunner.takeScreenshot;

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

  protected static String screenshot() {
    if (!Configuration.screenshots) {
      LOG.fine("Automatic screenshots are disabled.");
      return "";
    }

    String screenshot = takeScreenshot();
    if (Configuration.reportsUrl != null) {
      String screenshotRelativePath = screenshot.substring(System.getProperty("user.dir").length() + 1);
      String screenshotUrl = Configuration.reportsUrl + screenshotRelativePath;
      LOG.info("Replaced screenshot file path '" + screenshot + "' by public CI URL '" +
        screenshotUrl + "'");
      return "\nScreenshot: " + screenshotUrl;
    }

    LOG.info("reportsUrl is not configured. Returning screenshot file name '" + screenshot + "'");
    return "\nScreenshot: " + screenshot;
  }
}
