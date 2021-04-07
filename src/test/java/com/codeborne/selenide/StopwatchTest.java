package com.codeborne.selenide;

import org.junit.jupiter.api.RepeatedTest;

import static org.assertj.core.api.Assertions.assertThat;

final class StopwatchTest {
  private final Stopwatch stopwatch = new Stopwatch(100);

  @RepeatedTest(1000)
  void timeout_is_not_reached() throws InterruptedException {
    Thread.sleep(20);
    assertThat(stopwatch.isTimeoutReached()).isFalse();
  }

  @RepeatedTest(1000)
  void timeout_is_reached() throws InterruptedException {
    Thread.sleep(101);
    assertThat(stopwatch.isTimeoutReached()).isTrue();
  }
}
