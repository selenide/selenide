package com.codeborne.selenide.conditions.datetime;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Assert that element contains "value" attribute with date value that is satisfied to the provided `options`
 *
 * <p>Samples:
 * <br>
 *
 * {@code $("input").shouldHave(date(LocalDate.of(2020, 11, 12)).format("dd/MM/yyyy"));}
 * {@code $("input").shouldHave(date(withFormat("dd/MM/yyyy")));}
 * </p>
 */
public class DateConditions {
  private static final String DEFAULT_PATTERN = "yyyy-MM-dd";

  public static TemporalCondition<LocalDate> date(LocalDate date) {
    return date(date, DEFAULT_PATTERN);
  }

  public static TemporalCondition<LocalDate> date(LocalDate date, String pattern) {
    return new DateEquals(date, new DateFormatCondition(pattern));
  }

  public static TemporalCondition<LocalDate> date(LocalDate date, DateTimeFormatter format) {
    return new DateEquals(date, new DateFormatCondition(format));
  }

  public static TemporalCondition<LocalDate> dateBetween(LocalDate from, LocalDate until) {
    return dateBetween(from, until, DEFAULT_PATTERN);
  }

  public static TemporalCondition<LocalDate> dateBetween(LocalDate from, LocalDate until, String pattern) {
    return new DateBetween(from, until, new DateFormatCondition(pattern));
  }

  public static TemporalCondition<LocalDate> dateBetween(LocalDate from, LocalDate until, DateTimeFormatter format) {
    return new DateBetween(from, until, new DateFormatCondition(format));
  }

  public static TemporalFormatCondition<LocalDate> dateFormat(String pattern) {
    return new DateFormatCondition(pattern);
  }
}
