package com.codeborne.selenide;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

@ParametersAreNonnullByDefault
public class Stopwatch {
  private final Logger log;
  private final long startTimeNano;
  private final long timeoutNano;

  public Stopwatch(long timeoutMs) {
    this(timeoutMs, LoggerFactory.getLogger(Stopwatch.class));
  }

  Stopwatch(long timeoutMs, Logger log) {
    startTimeNano = nanoTime();
    timeoutNano = MILLISECONDS.toNanos(timeoutMs);
    this.log = log;
  }

  @CheckReturnValue
  public boolean isTimeoutReached() {
    long now = nanoTime();
    long elapsed = now - startTimeNano;
    boolean reached = elapsed > timeoutNano;
    if (log.isDebugEnabled()) {
      log.debug("started: {}, now: {}, elapsed: {} ({} ms), timeout: {} ms -> timeout reached: {}",
        startTimeNano, now, elapsed,
        NANOSECONDS.toMillis(elapsed),
        NANOSECONDS.toMillis(timeoutNano),
        reached);
    }
    return reached;
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
