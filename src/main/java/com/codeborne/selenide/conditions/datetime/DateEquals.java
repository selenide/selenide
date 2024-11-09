package com.codeborne.selenide.conditions.datetime;

import java.time.LocalDate;

class DateEquals extends TemporalCondition<LocalDate> {

  private final LocalDate expectedDate;

  DateEquals(LocalDate expectedDate, String pattern) {
    this(expectedDate, new DateFormatCondition(pattern));
  }

  DateEquals(LocalDate expectedDate, DateFormatCondition format) {
    super("date value", format);
    this.expectedDate = expectedDate;
  }

  @Override
  protected boolean matches(LocalDate actualDate) {
    return expectedDate.isEqual(actualDate);
  }

  @Override
  public String toString() {
    return String.format("%s \"%s\"", getName(), format(expectedDate));
  }
}
