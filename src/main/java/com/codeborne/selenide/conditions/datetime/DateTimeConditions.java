package com.codeborne.selenide.conditions.datetime;

import java.time.LocalDateTime;

/**
 * Assert that element contains "value" attribute with datetime value
 * that is satisfied to the provided `options`
 *
 * <p>Samples:
 * <br>
 *
 * {@code $("input").shouldHave(datetime(LocalDateTime.of(2020, 11, 12, 1, 1, 1).format("dd/MM/yyyy HH:mm:ss"));}
 * {@code $("input").shouldHave(datetime("dd/MM/yyyy HH:mm:ss"));}
 * </p>
 */
public class DateTimeConditions {
  private static final String DEFAULT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

  public static TemporalCondition<LocalDateTime> dateTime(LocalDateTime dateTime) {
    return dateTime(dateTime, DEFAULT_PATTERN);
  }

  public static TemporalCondition<LocalDateTime> dateTime(LocalDateTime dateTime, String pattern) {
    return new DateTimeEquals(dateTime, new DateTimeFormatCondition(pattern));
  }

  public static TemporalCondition<LocalDateTime> dateTimeBetween(LocalDateTime from, LocalDateTime until) {
    return dateTimeBetween(from, until, DEFAULT_PATTERN);
  }

  public static TemporalCondition<LocalDateTime> dateTimeBetween(LocalDateTime from, LocalDateTime until, String pattern) {
    return new DateTimeBetween(from, until, new DateTimeFormatCondition(pattern));
  }

  public static TemporalFormatCondition<LocalDateTime> dateTimeFormat(String pattern) {
    return new DateTimeFormatCondition(pattern);
  }
}
