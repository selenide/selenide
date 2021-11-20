package com.codeborne.selenide;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

final class StopwatchTest {
  private static final Logger log = LoggerFactory.getLogger(StopwatchTest.class);
  private final Stopwatch stopwatch = new Stopwatch(100, log);

  @Test
  void timeout_is_not_reached() {
    stopwatch.sleep(20);
    assertThat(stopwatch.isTimeoutReached()).isFalse();
  }

  @Test
  void timeout_is_reached() {
    stopwatch.sleep(101);
    assertThat(stopwatch.isTimeoutReached()).isTrue();
  }
}
