package com.codeborne.selenide.conditions.date;

import com.codeborne.selenide.Condition;

public class DateTimeFormatConditionOptions implements DateTimeConditionOptions {

  private final LocalDateTimeFormatCondition formatter;

  DateTimeFormatConditionOptions(LocalDateTimeFormatCondition formatter) {
    this.formatter = formatter;
  }

  @Override
  public Condition condition() {
    return formatter;
  }
}
