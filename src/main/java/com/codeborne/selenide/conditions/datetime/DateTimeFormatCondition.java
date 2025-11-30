package com.codeborne.selenide.conditions.datetime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQuery;

class DateTimeFormatCondition extends TemporalFormatCondition<LocalDateTime> {

  DateTimeFormatCondition(String pattern) {
    super("datetime format", pattern);
  }

  DateTimeFormatCondition(DateTimeFormatter format) {
    super("datetime format", format);
  }

  @Override
  protected TemporalQuery<LocalDateTime> queryFromTemporal() {
    return LocalDateTime::from;
  }
}
