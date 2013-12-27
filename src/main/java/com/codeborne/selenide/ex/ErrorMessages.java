package com.codeborne.selenide.ex;

import com.codeborne.selenide.Configuration;

import static com.codeborne.selenide.WebDriverRunner.takeScreenshot;

public class ErrorMessages {
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
    String screenshot = takeScreenshot();
    if (Configuration.reportsUrl != null) {
      screenshot = screenshot.substring(System.getProperty("user.dir").length() + 1);
      screenshot = Configuration.reportsUrl + screenshot;
    }
    return "\nScreenshot: " + screenshot;
  }
}
