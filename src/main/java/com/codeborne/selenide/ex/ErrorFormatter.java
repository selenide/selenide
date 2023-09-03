package com.codeborne.selenide.ex;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.Screenshot;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ErrorFormatter {
  @CheckReturnValue
  @Nonnull
  String generateErrorDetails(AssertionError error, Driver driver, Screenshot screenshot, long timeoutMs);

  @CheckReturnValue
  @Nonnull
  default String formatActualValue(@Nullable String actualValue) {
    return actualValue == null ? "" : String.format("Actual value: %s", actualValue);
  }

  @CheckReturnValue
  @Nonnull
  String actualValue(WebElementCondition condition, Driver driver,
                     @Nullable WebElement element,
                     @Nullable CheckResult lastCheckResult);
}
