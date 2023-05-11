package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import org.openqa.selenium.WebElement;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;

import static com.codeborne.selenide.CheckResult.Verdict.REJECT;

@ParametersAreNonnullByDefault
public class LocalDateCondition extends Condition {
  public static final String DEFAULT_PATTERN = "yyyy-MM-dd";

  private final LocalDate expectedDate;
  private final LocalDateFormatCondition formatCondition;

  public LocalDateCondition(LocalDate expectedDate, String pattern) {
    this(expectedDate, new LocalDateFormatCondition(pattern));
  }

  LocalDateCondition(LocalDate expectedDate, LocalDateFormatCondition formatCondition) {
    super("date value");
    this.expectedDate = expectedDate;
    this.formatCondition = formatCondition;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    CheckResult formatted = formatCondition.check(driver, element);

    if (formatted.verdict() == REJECT) return formatted;

    LocalDate localDateValue = (LocalDate) formatted.actualValue();

    return new CheckResult(expectedDate.isEqual(localDateValue), formatCondition.formatLocalDate(localDateValue));
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("%s: \"%s\" (with %s)", getName(), formatCondition.formatLocalDate(expectedDate), formatCondition);
  }
}
