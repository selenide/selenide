package com.codeborne.selenide.conditions.datetime;

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
public class LocalDateEqualCondition extends Condition {

  private final LocalDate expectedDate;
  private final LocalDateFormatCondition formatCondition;

  public LocalDateEqualCondition(LocalDate expectedDate, String pattern) {
    this(expectedDate, new LocalDateFormatCondition(pattern));
  }

  LocalDateEqualCondition(LocalDate expectedDate, LocalDateFormatCondition formatCondition) {
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

    if (localDateValue == null) throw new IllegalStateException("Format condition returns null, not date");

    return new CheckResult(expectedDate.isEqual(localDateValue), formatCondition.format(localDateValue));
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("%s: \"%s\" (with %s)", getName(), formatCondition.format(expectedDate), formatCondition);
  }
}
