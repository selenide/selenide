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
public class LocalDateBetweenCondition extends Condition {
  private final LocalDate startDate;
  private final LocalDate endDate;
  private final LocalDateFormatCondition formatCondition;

  public LocalDateBetweenCondition(LocalDate startDate, LocalDate endDate, String pattern) {
    this(startDate, endDate, new LocalDateFormatCondition(pattern));
  }

  LocalDateBetweenCondition(LocalDate startDate, LocalDate endDate, LocalDateFormatCondition formatCondition) {
    super("date value between");
    if (!endDate.isAfter(startDate)) throw new IllegalArgumentException("startDate must be before endDate");
    this.startDate = startDate;
    this.endDate = endDate;
    this.formatCondition = formatCondition;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    CheckResult formatted = formatCondition.check(driver, element);

    if (formatted.verdict() == REJECT) return formatted;

    LocalDate localDateValue = (LocalDate) formatted.actualValue();

    if (localDateValue == null) throw new IllegalStateException("Format condition returns null, but date");

    String actualValue = formatCondition.format(localDateValue);
    return new CheckResult((long) localDateValue.compareTo(endDate) * localDateValue.compareTo(startDate) <= 0, actualValue);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("%s [\"%s\", \"%s\"] (with %s)",
      getName(),
      formatCondition.format(startDate),
      formatCondition.format(endDate),
      formatCondition
      );
  }
}
