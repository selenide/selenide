package com.codeborne.selenide.conditions.datetime;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;

@ParametersAreNonnullByDefault
class DateBetween extends TemporalCondition<LocalDate> {
  private final LocalDate from;
  private final LocalDate until;

  DateBetween(LocalDate from, LocalDate until, String pattern) {
    this(from, until, new DateFormatCondition(pattern));
  }

  DateBetween(LocalDate from, LocalDate until, DateFormatCondition format) {
    super("date between", format);
    if (!until.isAfter(from)) {
      throw new IllegalArgumentException(String.format("from (%s) must be before until (%s)", from, until));
    }
    this.from = from;
    this.until = until;
  }

  @Override
  protected boolean matches(LocalDate actualDate) {
    return !actualDate.isAfter(until) && !actualDate.isBefore(from);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("%s \"%s\" and \"%s\"", getName(), format(from), format(until));
  }
}
