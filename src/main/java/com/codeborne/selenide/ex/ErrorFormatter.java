package com.codeborne.selenide.ex;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.impl.Screenshot;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.apache.commons.lang3.StringUtils.substring;

public interface ErrorFormatter {
  @CheckReturnValue
  @Nonnull
  String generateErrorDetails(AssertionError error, Driver driver, Screenshot screenshot, long timeoutMs);

  @CheckReturnValue
  @Nonnull
  default <T> String formatActualValue(@Nullable String actualValue) {
    return actualValue == null ? "" : String.format("Actual value: %s", actualValue);
  }

  @CheckReturnValue
  @Nonnull
  String actualValue(Condition condition, Driver driver,
                     @Nullable WebElement element,
                     @Nullable CheckResult lastCheckResult);

  @CheckReturnValue
  @Nullable
  default <T> String extractActualValue(ObjectCondition<T> condition, @Nonnull T object) {
    try {
      return condition.actualValue(object);
    }
    catch (RuntimeException failedToGetValue) {
      String failedActualValue = failedToGetValue.getClass().getSimpleName() + ": " + failedToGetValue.getMessage();
      return substring(failedActualValue, 0, 50);
    }
  }
}
