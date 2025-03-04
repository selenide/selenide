package com.codeborne.selenide.conditions.datetime;

import java.time.LocalDate;
import java.time.temporal.TemporalQuery;

class DateFormatCondition extends TemporalFormatCondition<LocalDate> {

  DateFormatCondition(String pattern) {
    super("date format", pattern);
  }

  @Override
  protected TemporalQuery<LocalDate> queryFromTemporal() {
    return LocalDate::from;
  }
}
