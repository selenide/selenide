package com.codeborne.selenide.ex;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.Screenshot;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

public interface ErrorFormatter {
  String generateErrorDetails(AssertionError error, Driver driver, Screenshot screenshot, long timeoutMs);

  String causedBy(@Nullable Throwable cause);

  default String formatActualValue(@Nullable String actualValue) {
    return actualValue == null ? "" : String.format("Actual value: %s", actualValue);
  }

  String actualValue(WebElementCondition condition, Driver driver,
                     @Nullable WebElement element,
                     @Nullable CheckResult lastCheckResult);
}
