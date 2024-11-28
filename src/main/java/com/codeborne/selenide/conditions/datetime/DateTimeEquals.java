package com.codeborne.selenide.conditions.datetime;

import java.time.LocalDateTime;

class DateTimeEquals extends TemporalCondition<LocalDateTime> {

  private final LocalDateTime expectedDateTime;

  DateTimeEquals(LocalDateTime expectedDateTime, String pattern) {
    this(expectedDateTime, new DateTimeFormatCondition(pattern));
  }

  DateTimeEquals(LocalDateTime expectedDateTime, DateTimeFormatCondition format) {
    super("datetime value", format);
    this.expectedDateTime = expectedDateTime;
  }

  @Override
  protected boolean matches(LocalDateTime actualDateTime) {
    return expectedDateTime.isEqual(actualDateTime);
  }

  @Override
  public String toString() {
    return String.format("%s \"%s\"", getName(), format(expectedDateTime));
  }
}
