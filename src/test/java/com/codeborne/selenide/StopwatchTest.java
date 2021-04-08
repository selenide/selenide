package com.codeborne.selenide;

import org.junit.jupiter.api.RepeatedTest;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

final class StopwatchTest {
  private final Stopwatch stopwatch = new Stopwatch(100);
  private final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSSZ");

  @RepeatedTest(1000)
  void timeout_is_not_reached() throws InterruptedException {
    Date start = new Date();
    Thread.sleep(20);
    Date end = new Date();
    assertThat(stopwatch.isTimeoutReached()).as(() -> description(start, end)).isFalse();
  }

  @RepeatedTest(1000)
  void timeout_is_reached() throws InterruptedException {
    Date start = new Date();
    Thread.sleep(101);
    Date end = new Date();
    assertThat(stopwatch.isTimeoutReached()).as(() -> description(start, end)).isTrue();
  }

  @Nonnull
  @CheckReturnValue
  private String description(Date start, Date end) {
    return "*************** Started at: " + df.format(start) + ", ended at: " + df.format(end) +
      ", duration: " + (end.getTime() - start.getTime()) + " ms.";
  }
}
