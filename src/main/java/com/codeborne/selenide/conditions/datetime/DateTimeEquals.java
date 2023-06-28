package com.codeborne.selenide.conditions.datetime;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDateTime;

@ParametersAreNonnullByDefault
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

  @Nonnull
  @CheckReturnValue
  @Override
  public String toString() {
    return String.format("%s \"%s\"", getName(), format(expectedDateTime));
  }
}
