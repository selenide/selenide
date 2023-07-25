package com.codeborne.selenide.conditions.datetime;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;

/**
 * Assert that element contains "value" attribute with date value that is satisfied to the provided `options`
 *
 * <p>Samples:
 * <br>
 *
 * {@code $("input").shouldHave(date(LocalDate.of(2020, 11, 12)).format("dd/MM/yyyy"));}
 * {@code $("input").shouldHave(date(withFormat("dd/MM/yyyy")));}
 * </p>
 *
 * @since 6.16.0
 */
@ParametersAreNonnullByDefault
public class DateConditions {
  private static final String DEFAULT_PATTERN = "yyyy-MM-dd";

  @Nonnull
  @CheckReturnValue
  public static TemporalCondition<LocalDate> date(LocalDate date) {
    return date(date, DEFAULT_PATTERN);
  }

  @Nonnull
  @CheckReturnValue
  public static TemporalCondition<LocalDate> date(LocalDate date, String pattern) {
    return new DateEquals(date, new DateFormatCondition(pattern));
  }

  @Nonnull
  @CheckReturnValue
  public static TemporalCondition<LocalDate> dateBetween(LocalDate from, LocalDate until) {
    return dateBetween(from, until, DEFAULT_PATTERN);
  }

  @Nonnull
  @CheckReturnValue
  public static TemporalCondition<LocalDate> dateBetween(LocalDate from, LocalDate until, String pattern) {
    return new DateBetween(from, until, new DateFormatCondition(pattern));
  }

  @Nonnull
  @CheckReturnValue
  public static TemporalFormatCondition<LocalDate> dateFormat(String pattern) {
    return new DateFormatCondition(pattern);
  }
}
