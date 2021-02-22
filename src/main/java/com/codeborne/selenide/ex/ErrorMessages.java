package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.DurationFormat;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.apache.commons.lang3.StringUtils.substring;

@ParametersAreNonnullByDefault
public class ErrorMessages {
  private static final DurationFormat df = new DurationFormat();

  @CheckReturnValue
  protected static String timeout(long timeoutMs) {
    return String.format("%nTimeout: %s", df.format(timeoutMs));
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
