package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDateTime;

import static com.codeborne.selenide.CheckResult.Verdict.REJECT;

@ParametersAreNonnullByDefault
public class LocalDateTimeCondition extends Condition {
  public static final String DEFAULT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

  private final LocalDateTime expectedDateTime;
  private final LocalDateTimeFormatCondition formatCondition;

  public LocalDateTimeCondition(LocalDateTime expectedDateTime, String pattern) {
    this(expectedDateTime, new LocalDateTimeFormatCondition(pattern));
  }

  LocalDateTimeCondition(LocalDateTime expectedDateTime, LocalDateTimeFormatCondition formatCondition) {
    super("datetime value");
    this.expectedDateTime = expectedDateTime;
    this.formatCondition = formatCondition;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    CheckResult formatted = formatCondition.check(driver, element);

    if (formatted.verdict() == REJECT) return formatted;

    LocalDateTime localDateValue = (LocalDateTime) formatted.actualValue();

    return new CheckResult(expectedDateTime.isEqual(localDateValue), formatCondition.formatLocalDateTime(localDateValue));
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("%s: \"%s\" (with %s)", getName(), formatCondition.formatLocalDateTime(expectedDateTime), formatCondition);
  }
}
