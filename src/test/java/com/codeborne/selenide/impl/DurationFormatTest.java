package com.codeborne.selenide.impl;

import com.codeborne.selenide.UseLocaleExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

final class DurationFormatTest {
  private final DurationFormat df = new DurationFormat();

  @RegisterExtension
  static UseLocaleExtension useLocale = new UseLocaleExtension("en");

  @Test
  void zero() {
    assertThat(df.format(0)).isEqualTo("0ms");
  }

  @Test
  void lessThanSecond() {
    assertThat(df.format(1)).isEqualTo("1ms");
    assertThat(df.format(999)).isEqualTo("999ms");
  }

  @Test
  void integerSeconds() {
    assertThat(df.format(1000)).isEqualTo("1s");
    assertThat(df.format(2000)).isEqualTo("2s");
  }

  @Test
  void greaterThanSecond() {
    assertThat(df.format(1500)).isEqualTo("1.5s");
    assertThat(df.format(1567)).isEqualTo("1.567s");
    assertThat(df.format(2001)).isEqualTo("2.001s");
  }

  @Test
  void greaterThanMinute() {
    assertThat(df.format(Duration.ofMinutes(1))).isEqualTo("60s");
    assertThat(df.format(Duration.ofMinutes(2).plusSeconds(2))).isEqualTo("122s");
    assertThat(df.format(Duration.ofMinutes(3).plusMillis(300))).isEqualTo("180.3s");
    assertThat(df.format(Duration.ofMinutes(3).plusMillis(330))).isEqualTo("180.33s");
    assertThat(df.format(Duration.ofMinutes(3).plusMillis(333))).isEqualTo("180.333s");
    assertThat(df.format(Duration.ofMinutes(3).plusNanos(333_333_333))).isEqualTo("180.333s");
    assertThat(df.format(Duration.ofMinutes(3).plusNanos(333_999_999))).isEqualTo("180.333s");
  }
}
