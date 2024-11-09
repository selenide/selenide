package com.codeborne.selenide.ex;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.DurationFormat;
import com.codeborne.selenide.impl.Screenshot;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.ex.Strings.join;

public class SelenideErrorFormatter implements ErrorFormatter {
  private static final DurationFormat df = new DurationFormat();

  @Override
  public String generateErrorDetails(AssertionError error, Driver driver, Screenshot screenshot, long timeoutMs) {
    return join(screenshot.summary(), timeout(timeoutMs), causedBy(error.getCause()));
  }

  protected String timeout(long timeoutMs) {
    return String.format("Timeout: %s", df.format(timeoutMs));
  }

  @Override
  public String actualValue(WebElementCondition condition, Driver driver,
                            @Nullable WebElement element,
                            @Nullable CheckResult lastCheckResult) {
    if (lastCheckResult != null && lastCheckResult.actualValue() != null) {
      return String.format("Actual value: %s", lastCheckResult.actualValue());
    }

    return "";
  }

  @Override
  public String causedBy(@Nullable Throwable cause) {
    if (cause == null) {
      return "";
    }
    return String.format("Caused by: %s", Cleanup.of.webdriverExceptionMessage(cause));
  }
}
