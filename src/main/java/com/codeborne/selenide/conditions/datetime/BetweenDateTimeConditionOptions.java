package com.codeborne.selenide.conditions.datetime;

import com.codeborne.selenide.Condition;

import java.time.LocalDateTime;

public class BetweenDateTimeConditionOptions implements DateTimeConditionOptions {

  private final LocalDateTime startDateTime;
  private final LocalDateTime endDateTime;
  private final LocalDateTimeFormatCondition formatter;

  BetweenDateTimeConditionOptions(LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDateTimeFormatCondition formatter) {
    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.formatter = formatter;
  }

  @Override
  public Condition condition() {
    return new LocalDateTimeBetweenCondition(startDateTime, endDateTime, formatter);
  }

  public DateTimeConditionOptions format(String pattern) {
    return new BetweenDateTimeConditionOptions(startDateTime, endDateTime, new LocalDateTimeFormatCondition(pattern));
  }
}
