package com.codeborne.selenide.conditions.datetime;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDateTime;
import java.time.temporal.TemporalQuery;

@ParametersAreNonnullByDefault
public class LocalDateTimeFormatCondition extends TemporalFormatCondition<LocalDateTime> {

  public LocalDateTimeFormatCondition(String pattern) {
    super("datetime value format", pattern);
  }

  @Override
  protected TemporalQuery<LocalDateTime> queryFromTemporal() {
    return LocalDateTime::from;
  }
}
