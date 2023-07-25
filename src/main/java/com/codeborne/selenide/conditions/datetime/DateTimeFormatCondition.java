package com.codeborne.selenide.conditions.datetime;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDateTime;
import java.time.temporal.TemporalQuery;

@ParametersAreNonnullByDefault
class DateTimeFormatCondition extends TemporalFormatCondition<LocalDateTime> {

  DateTimeFormatCondition(String pattern) {
    super("datetime format", pattern);
  }

  @Nonnull
  @CheckReturnValue
  @Override
  protected TemporalQuery<LocalDateTime> queryFromTemporal() {
    return LocalDateTime::from;
  }
}
