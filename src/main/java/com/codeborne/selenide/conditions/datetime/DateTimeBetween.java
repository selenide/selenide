package com.codeborne.selenide.conditions.datetime;

import java.time.LocalDateTime;

class DateTimeBetween extends TemporalCondition<LocalDateTime> {
  private final LocalDateTime from;
  private final LocalDateTime until;

  DateTimeBetween(LocalDateTime from, LocalDateTime until, String pattern) {
    this(from, until, new DateTimeFormatCondition(pattern));
  }

  DateTimeBetween(LocalDateTime from, LocalDateTime until, DateTimeFormatCondition formatCondition) {
    super("datetime value between", formatCondition);
    if (!until.isAfter(from)) {
      throw new IllegalArgumentException(String.format("from (%s) must be before until (%s)", from, until));
    }
    this.from = from;
    this.until = until;
  }

  @Override
  protected boolean matches(LocalDateTime actualDate) {
    return !actualDate.isAfter(until) && !actualDate.isBefore(from);
  }

  @Override
  public String toString() {
    return String.format("%s \"%s\" and \"%s\"", getName(), format(from), format(until));
  }
}
