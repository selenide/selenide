package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Context;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.List;

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

  static String actualValue(Condition condition, Context context, WebElement element) {
    if (element != null) {
      String actualValue = condition.actualValue(context, element);
      if (actualValue != null) {
        return "\nActual value: " + actualValue;
      }
    }
    return "";
  }

  public static String screenshot(Context context) {
    return screenshot(ScreenShotLaboratory.getInstance().formatScreenShotPath(context));
  }
  
  public static String screenshot(String screenshotPath) {
    if (!Configuration.screenshots) {
      return "";
    }

    if (screenshotPath == null || screenshotPath.isEmpty()) {
      return "\nScreenshot: " + screenshotPath;
    }

    if (Configuration.savePageSource && !screenshotPath.endsWith(".html")) {
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

  static String jsErrors(List<String> jsErrors) {
    if (jsErrors == null || jsErrors.isEmpty()) {
      return "";
    }
    return "\nJavascript Errors: " + jsErrors;
  }

  private static String getHtmlFilePath(String screenshotPath) {
    return screenshotPath.substring(0, screenshotPath.lastIndexOf('.')) + ".html";
  }
}
