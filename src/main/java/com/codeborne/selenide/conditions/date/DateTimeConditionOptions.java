package com.codeborne.selenide.conditions.date;

import com.codeborne.selenide.Condition;

import java.time.LocalDateTime;

public interface DateTimeConditionOptions {
  String DEFAULT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

  Condition condition();

  static EqDateTimeConditionOptions eq(LocalDateTime DateTime) {
    return new EqDateTimeConditionOptions(DateTime, new LocalDateTimeFormatCondition(DEFAULT_PATTERN));
  }

  static EqDateTimeConditionOptions eq(LocalDateTime DateTime, String pattern) {
    return new EqDateTimeConditionOptions(DateTime, new LocalDateTimeFormatCondition(pattern));
  }

  static BetweenDateTimeConditionOptions between(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    return new BetweenDateTimeConditionOptions(startDateTime, endDateTime, new LocalDateTimeFormatCondition(DEFAULT_PATTERN));
  }

  static BetweenDateTimeConditionOptions between(LocalDateTime startDateTime, LocalDateTime endDateTime, String pattern) {
    return new BetweenDateTimeConditionOptions(startDateTime, endDateTime, new LocalDateTimeFormatCondition(pattern));
  }

  static DateTimeFormatConditionOptions withFormat(String pattern) {
    return new DateTimeFormatConditionOptions(new LocalDateTimeFormatCondition(pattern));
  }

}
