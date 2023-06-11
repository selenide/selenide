package com.codeborne.selenide.conditions.datetime;

import com.codeborne.selenide.Condition;

import java.time.LocalDate;

public interface DateConditionOptions {
  String DEFAULT_PATTERN = "yyyy-MM-dd";

  Condition condition();

  static EqDateConditionOptions eq(LocalDate date) {
    return new EqDateConditionOptions(date, new LocalDateFormatCondition(DEFAULT_PATTERN));
  }

  static EqDateConditionOptions eq(LocalDate date, String pattern) {
    return new EqDateConditionOptions(date, new LocalDateFormatCondition(pattern));
  }

  static BetweenDateConditionOptions between(LocalDate startDate, LocalDate endDate) {
    return new BetweenDateConditionOptions(startDate, endDate, new LocalDateFormatCondition(DEFAULT_PATTERN));
  }

  static BetweenDateConditionOptions between(LocalDate startDate, LocalDate endDate, String pattern) {
    return new BetweenDateConditionOptions(startDate, endDate, new LocalDateFormatCondition(pattern));
  }

  static DateFormatConditionOptions withFormat(String pattern) {
    return new DateFormatConditionOptions(new LocalDateFormatCondition(pattern));
  }

}


