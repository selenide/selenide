package com.codeborne.selenide.conditions.date;

import com.codeborne.selenide.Condition;

import java.time.LocalDate;

public class EqDateConditionOptions implements DateConditionOptions {

  private final LocalDate expected;
  private final LocalDateFormatCondition formatter;

  EqDateConditionOptions(LocalDate expected, LocalDateFormatCondition formatter) {
    this.expected = expected;
    this.formatter = formatter;
  }

  @Override
  public Condition condition() {
    return new LocalDateEqualCondition(expected, formatter);
  }

  public DateConditionOptions format(String pattern) {
    return new EqDateConditionOptions(expected, new LocalDateFormatCondition(pattern));
  }
}
