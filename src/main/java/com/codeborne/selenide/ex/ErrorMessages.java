package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.impl.Cleanup;
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
    return screenshot(Screenshots.screenshots.formatScreenShotPath());
  }
  
  public static String screenshot(String screenshotPath) {
    if (!Configuration.screenshots) {
      return "";
    }
    if(Configuration.savePageSource) {
       if(screenshotPath == null || screenshotPath.isEmpty()) {
          return "\nScreenshot: " + screenshotPath;
       }
       return "\nScreenshot: " + screenshotPath
              + "\nHtml: " + screenshotPath.substring(0, screenshotPath.lastIndexOf('.')) + ".html";
    }else {
       return "\nScreenshot: " + screenshotPath;
    }
  }

  public static String causedBy(Throwable cause) {
    if (cause == null) {
      return "";
    }
    if (cause instanceof WebDriverException) {
      return "\nCaused by: " + Cleanup.of.webdriverExceptionMessage(cause);
    }
    return "\nCaused by: " + cause;
  }

  public static String jsErrors(List<String> jsErrors) {
    if (jsErrors == null || jsErrors.isEmpty()) {
      return "";
    }
    return "\nJavascript Errors: " + jsErrors;
  }
}
