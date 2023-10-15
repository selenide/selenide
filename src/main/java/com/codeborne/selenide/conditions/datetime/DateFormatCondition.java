package com.codeborne.selenide.conditions.datetime;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;
import java.time.temporal.TemporalQuery;

@ParametersAreNonnullByDefault
class DateFormatCondition extends TemporalFormatCondition<LocalDate> {

  DateFormatCondition(String pattern) {
    super("date format", pattern);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  protected TemporalQuery<LocalDate> queryFromTemporal() {
    return LocalDate::from;
  }
}
