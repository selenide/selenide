package com.codeborne.selenide.ex;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.DurationFormat;
import com.codeborne.selenide.impl.Screenshot;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ex.Strings.join;
import static org.apache.commons.lang3.StringUtils.substring;

@ParametersAreNonnullByDefault
public class SelenideErrorFormatter implements ErrorFormatter {
  private static final DurationFormat df = new DurationFormat();

  @CheckReturnValue
  @Nonnull
  @Override
  public String generateErrorDetails(AssertionError error, Driver driver, Screenshot screenshot, long timeoutMs) {
    return join(screenshot.summary(), timeout(timeoutMs), causedBy(error.getCause()));
  }

  @CheckReturnValue
  @Nonnull
  protected String timeout(long timeoutMs) {
    return String.format("Timeout: %s", df.format(timeoutMs));
  }

  @CheckReturnValue
  @Nonnull
  @Override
  public String actualValue(Condition condition, Driver driver,
                            @Nullable WebElement element,
                            @Nullable CheckResult lastCheckResult) {
    if (lastCheckResult != null && lastCheckResult.actualValue() != null) {
      return String.format("Actual value: %s", lastCheckResult.actualValue());
    }

    // Deprecated branch for custom condition (not migrated to CheckResult):
    String actualValue = extractActualValue(condition, driver, element);
    if (actualValue != null) {
      return String.format("Actual value: %s", actualValue);
    }
    return "";
  }

  @Nullable
  @CheckReturnValue
  protected String extractActualValue(Condition condition, Driver driver, @Nullable WebElement element) {
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
  protected <T> String actualValue(ObjectCondition<T> condition, @Nullable T object) {
    if (object == null) {
      return "";
    }
    return formatActualValue(extractActualValue(condition, object));
  }

  @CheckReturnValue
  @Nonnull
  protected String causedBy(@Nullable Throwable cause) {
    if (cause == null) {
      return "";
    }
    return String.format("Caused by: %s", Cleanup.of.webdriverExceptionMessage(cause));
  }
}
