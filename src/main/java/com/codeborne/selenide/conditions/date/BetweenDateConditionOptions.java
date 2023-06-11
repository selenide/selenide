package com.codeborne.selenide.conditions.date;

import com.codeborne.selenide.Condition;

import java.time.LocalDate;

public class BetweenDateConditionOptions implements DateConditionOptions {

  private final LocalDate startDate;
  private final LocalDate endDate;
  private final LocalDateFormatCondition formatter;

  BetweenDateConditionOptions(LocalDate startDate, LocalDate endDate, LocalDateFormatCondition formatter) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.formatter = formatter;
  }

  @Override
  public Condition condition() {
    return new LocalDateBetweenCondition(startDate, endDate, formatter);
  }

  public DateConditionOptions format(String pattern) {
    return new BetweenDateConditionOptions(startDate, endDate, new LocalDateFormatCondition(pattern));
  }
}
