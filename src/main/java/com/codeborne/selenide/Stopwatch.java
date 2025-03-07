package com.codeborne.selenide;

import java.time.Duration;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Stopwatch {
  private final long startTimeNano;
  private final long timeoutNano;

  public Stopwatch(Duration timeout) {
    this(timeout.toMillis());
  }

  public Stopwatch(long timeoutMs) {
    this(timeoutMs, nanoTime());
  }

  Stopwatch(long timeoutMs, long startTimeNano) {
    this.startTimeNano = startTimeNano;
    timeoutNano = MILLISECONDS.toNanos(timeoutMs);
  }

  public boolean isTimeoutReached() {
    return isTimeoutReached(nanoTime());
  }

  public long getElapsedTimeMs() {
    return Duration.ofNanos(nanoTime() - startTimeNano).toMillis();
  }

  boolean isTimeoutReached(long now) {
    return now - startTimeNano > timeoutNano;
  }

  public void sleep(long milliseconds) {
    if (isTimeoutReached()) return;

    try {
      Thread.sleep(milliseconds);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  /**
   * Sleep at least given number of milliseconds.
   * Default {@link Thread#sleep(long)} doesn't guarantee the sleep duration, it can awake earlier.
   *
   * @param milliseconds the number of milliseconds to sleep
   */
  public static void sleepAtLeast(long milliseconds) {
    Stopwatch stopwatch = new Stopwatch(milliseconds);
    do {
      stopwatch.sleep(milliseconds);
    }
    while (!stopwatch.isTimeoutReached());
  }
}
