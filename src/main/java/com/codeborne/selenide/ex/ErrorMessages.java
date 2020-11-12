package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Config;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.apache.commons.lang3.StringUtils.substring;

@ParametersAreNonnullByDefault
public class ErrorMessages {
  @CheckReturnValue
  protected static String timeout(long timeoutMs) {
    if (timeoutMs < 1000) {
      return String.format("%nTimeout: %d ms.", timeoutMs);
    }
    if (timeoutMs % 1000 == 0) {
      return String.format("%nTimeout: %d s.", timeoutMs / 1000);
    }

    return String.format("%nTimeout: %.3f s.", timeoutMs / 1000.0);
  }

  @CheckReturnValue
  static String actualValue(Condition condition, Driver driver, @Nullable WebElement element) {
    if (element != null) {
      try {
        String actualValue = condition.actualValue(driver, element);
        if (actualValue != null) {
          return String.format("%nActual value: %s", actualValue);
        }
      }
      catch (RuntimeException failedToGetValue) {
        String failedActualValue = failedToGetValue.getClass().getSimpleName() + ": " + failedToGetValue.getMessage();
        return String.format("%nActual value: %s", substring(failedActualValue, 0, 50));
      }
    }
    return "";
  }

  @CheckReturnValue
  public static String screenshot(Driver driver) {
    return screenshot(driver.config(), ScreenShotLaboratory.getInstance().formatScreenShotPath(driver));
  }

  @CheckReturnValue
  public static String screenshot(Config config, @Nullable String screenshotPath) {
    if (!config.screenshots()) {
      return "";
    }

    if (screenshotPath == null || screenshotPath.isEmpty()) {
      return String.format("%nScreenshot: %s", screenshotPath);
    }

    if (config.savePageSource() && !screenshotPath.endsWith(".html")) {
      String htmlFilePath = getHtmlFilePath(screenshotPath);
      return String.format("%nScreenshot: %s%nPage source: %s", screenshotPath, htmlFilePath);
    }
    else if (screenshotPath.endsWith(".html")) {
      return String.format("%nPage source: %s", screenshotPath);
    }
    else {
      return String.format("%nScreenshot: %s", screenshotPath);
    }
  }

  @CheckReturnValue
  static String causedBy(@Nullable Throwable cause) {
    if (cause == null) {
      return "";
    }
    if (cause instanceof WebDriverException) {
      return String.format("%nCaused by: %s", Cleanup.of.webdriverExceptionMessage(cause));
    }
    return String.format("%nCaused by: %s", cause);
  }

  @CheckReturnValue
  private static String getHtmlFilePath(String screenshotPath) {
    return screenshotPath.substring(0, screenshotPath.lastIndexOf('.')) + ".html";
  }
}
