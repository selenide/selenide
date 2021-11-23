package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class StopwatchTest {
  private final long startedAt = 1_000_000_000;
  private final Stopwatch stopwatch = new Stopwatch(666, startedAt);

  @Test
  void timeout_is_not_reached() {
    assertThat(stopwatch.isTimeoutReached(1_000_000_000)).isFalse();
    assertThat(stopwatch.isTimeoutReached(1_000_000_001)).isFalse();
    assertThat(stopwatch.isTimeoutReached(1_666_000_000)).isFalse();
  }

  @Test
  void timeout_is_reached() {
    assertThat(stopwatch.isTimeoutReached(1_666_000_001)).isTrue();
    assertThat(stopwatch.isTimeoutReached(2_000_000_000)).isTrue();
  }
}
