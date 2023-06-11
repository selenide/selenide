package com.codeborne.selenide.conditions.date;

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
public class LocalDateTimeBetweenCondition extends Condition {
  private final LocalDateTime startDateTime;
  private final LocalDateTime endDateTime;
  private final LocalDateTimeFormatCondition formatCondition;

  public LocalDateTimeBetweenCondition(LocalDateTime startDateTime, LocalDateTime endDateTime, String pattern) {
    this(startDateTime, endDateTime, new LocalDateTimeFormatCondition(pattern));
  }

  LocalDateTimeBetweenCondition(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDateTimeFormatCondition formatCondition) {
    super("datetime value between");
    if (!endDateTime.isAfter(startDateTime)) throw new IllegalArgumentException("startDateTime must be before endDateTime");
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.formatCondition = formatCondition;
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public CheckResult check(Driver driver, WebElement element) {
    CheckResult formatted = formatCondition.check(driver, element);

    if (formatted.verdict() == REJECT) return formatted;

    LocalDateTime localDateValue = (LocalDateTime) formatted.actualValue();

    if (localDateValue == null) throw new IllegalStateException("Format condition returns null, not datetime");

    String actualValue = formatCondition.format(localDateValue);
    return new CheckResult((long) localDateValue.compareTo(endDateTime) * localDateValue.compareTo(startDateTime) <= 0, actualValue);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("%s [\"%s\", \"%s\"] (with %s)",
      getName(),
      formatCondition.format(startDateTime),
      formatCondition.format(endDateTime),
      formatCondition);
  }
}
