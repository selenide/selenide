package com.codeborne.selenide.ex;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.impl.Cleanup;
import com.codeborne.selenide.impl.DurationFormat;
import com.codeborne.selenide.impl.Screenshot;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ex.Strings.join;

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
  public String actualValue(WebElementCondition condition, Driver driver,
                            @Nullable WebElement element,
                            @Nullable CheckResult lastCheckResult) {
    if (lastCheckResult != null && lastCheckResult.actualValue() != null) {
      return String.format("Actual value: %s", lastCheckResult.actualValue());
    }

    return "";
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
