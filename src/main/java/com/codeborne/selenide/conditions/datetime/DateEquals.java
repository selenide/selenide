package com.codeborne.selenide.conditions.datetime;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;

@ParametersAreNonnullByDefault
class DateEquals extends TemporalCondition<LocalDate> {

  private final LocalDate expectedDate;

  DateEquals(LocalDate expectedDate, String pattern) {
    this(expectedDate, new DateFormatCondition(pattern));
  }

  DateEquals(LocalDate expectedDate, DateFormatCondition format) {
    super("date value", format);
    this.expectedDate = expectedDate;
  }

  @Override
  protected boolean matches(LocalDate actualDate) {
    return expectedDate.isEqual(actualDate);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("%s \"%s\"", getName(), format(expectedDate));
  }
}
