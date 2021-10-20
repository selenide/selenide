package com.codeborne.selenide.ex;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.DurationFormat;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.apache.commons.lang3.StringUtils.substring;

@ParametersAreNonnullByDefault
public class ErrorMessages {
  private static final DurationFormat df = new DurationFormat();

  @CheckReturnValue
  @Nonnull
  protected static String timeout(long timeoutMs) {
    return String.format("%nTimeout: %s", df.format(timeoutMs));
  }

  @CheckReturnValue
  @Nonnull
  static String actualValue(Condition condition, Driver driver,
                            @Nullable WebElement element,
                            @Nullable CheckResult lastCheckResult) {
    if (lastCheckResult != null && lastCheckResult.actualValue() != null) {
      return String.format("%nActual value: %s", lastCheckResult.actualValue());
    }

    // Deprecated branch for custom condition (not migrated to CheckResult):
    String actualValue = extractActualValue(condition, driver, element);
    if (actualValue != null) {
      return String.format("%nActual value: %s", actualValue);
    }
    return "";
  }

  @Nullable
  @CheckReturnValue
  private static String extractActualValue(Condition condition, Driver driver, @Nullable WebElement element) {
    if (element != null) {
      try {
        return condition.actualValue(driver, element);
      }
      catch (RuntimeException failedToGetValue) {
        String failedActualValue = failedToGetValue.getClass().getSimpleName() + ": " + failedToGetValue.getMessage();
        return substring(failedActualValue, 0, 50);
      }
    }
    return null;
  }

  @CheckReturnValue
  @Nonnull
  static <T> String actualValue(ObjectCondition<T> condition, @Nullable T object) {
    if (object == null) {
      return "";
    }
    return formatActualValue(extractActualValue(condition, object));
  }

  @CheckReturnValue
  @Nullable
  static <T> String extractActualValue(ObjectCondition<T> condition, @Nonnull T object) {
    try {
      return condition.actualValue(object);
    }
    catch (RuntimeException failedToGetValue) {
      String failedActualValue = failedToGetValue.getClass().getSimpleName() + ": " + failedToGetValue.getMessage();
      return substring(failedActualValue, 0, 50);
    }
  }

  @CheckReturnValue
  @Nonnull
  static <T> String formatActualValue(@Nullable String actualValue) {
    return actualValue == null ? "" : String.format("%nActual value: %s", actualValue);
  }

  @CheckReturnValue
  @Nonnull
  static String causedBy(@Nullable Throwable cause) {
    if (cause == null) {
      return "";
    }
    if (cause instanceof WebDriverException) {
      return String.format("%nCaused by: %s", Cleanup.of.webdriverExceptionMessage(cause));
    }
    return String.format("%nCaused by: %s", cause);
  }
}
