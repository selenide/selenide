package com.codeborne.selenide.impl;

import integration.UseLocaleExtension;
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
    assertThat(df.format(0)).isEqualTo("0 ms.");
  }

  @Test
  void lessThanSecond() {
    assertThat(df.format(1)).isEqualTo("1 ms.");
    assertThat(df.format(999)).isEqualTo("999 ms.");
  }

  @Test
  void integerSeconds() {
    assertThat(df.format(1000)).isEqualTo("1 s.");
    assertThat(df.format(2000)).isEqualTo("2 s.");
  }

  @Test
  void greaterThanSecond() {
    assertThat(df.format(1500)).isEqualTo("1.500 s.");
    assertThat(df.format(1567)).isEqualTo("1.567 s.");
    assertThat(df.format(2001)).isEqualTo("2.001 s.");
  }

  @Test
  void greaterThanMinute() {
    assertThat(df.format(Duration.ofMinutes(1))).isEqualTo("60 s.");
    assertThat(df.format(Duration.ofMinutes(2).plusSeconds(2))).isEqualTo("122 s.");
    assertThat(df.format(Duration.ofMinutes(3).plusMillis(300))).isEqualTo("180.300 s.");
  }
}
