package com.codeborne.selenide.conditions.date;

import com.codeborne.selenide.Condition;

import java.time.LocalDate;

public interface DateConditionOptions {

  Condition condition();

  static EqDateConditionOptions eq(LocalDate date) {
    return new EqDateConditionOptions(date, new LocalDateFormatCondition(LocalDateEqualCondition.DEFAULT_PATTERN));
  }

  static EqDateConditionOptions eq(LocalDate date, String pattern) {
    return new EqDateConditionOptions(date, new LocalDateFormatCondition(pattern));
  }

  static BetweenDateConditionOptions between(LocalDate startDate, LocalDate endDate) {
    return new BetweenDateConditionOptions(startDate, endDate, new LocalDateFormatCondition(LocalDateEqualCondition.DEFAULT_PATTERN));
  }

  static BetweenDateConditionOptions between(LocalDate startDate, LocalDate endDate, String pattern) {
    return new BetweenDateConditionOptions(startDate, endDate, new LocalDateFormatCondition(pattern));
  }

  static DateFormatConditionOptions withFormat(String pattern) {
    return new DateFormatConditionOptions(new LocalDateFormatCondition(pattern));
  }

}


