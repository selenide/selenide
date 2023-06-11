package com.codeborne.selenide.conditions.date;

import com.codeborne.selenide.Condition;

import java.time.LocalDateTime;

public class EqDateTimeConditionOptions implements DateTimeConditionOptions {

  private final LocalDateTime expected;
  private final LocalDateTimeFormatCondition formatter;

  EqDateTimeConditionOptions(LocalDateTime expected, LocalDateTimeFormatCondition formatter) {
    this.expected = expected;
    this.formatter = formatter;
  }

  @Override
  public Condition condition() {
    return new LocalDateTimeEqualCondition(expected, formatter);
  }

  public DateTimeConditionOptions format(String pattern) {
    return new EqDateTimeConditionOptions(expected, new LocalDateTimeFormatCondition(pattern));
  }
}
