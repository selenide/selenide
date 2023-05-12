package com.codeborne.selenide.conditions;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;
import java.time.temporal.TemporalQuery;

@ParametersAreNonnullByDefault
public class LocalDateFormatCondition extends TemporalFormatCondition<LocalDate> {

  public LocalDateFormatCondition(String pattern) {
    super("date value format", pattern);
  }

  @Override
  protected TemporalQuery<LocalDate> queryFromTemporal() {
    return LocalDate::from;
  }
}
