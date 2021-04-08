package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@ParametersAreNonnullByDefault
public class Stopwatch {
  private final long endTimeNano;

  public Stopwatch(long timeoutMs) {
    this.endTimeNano = nanoTime() + MILLISECONDS.toNanos(timeoutMs);
  }

  @CheckReturnValue
  public boolean isTimeoutReached() {
    long current = nanoTime();
    System.out.println("Current: " + current + ", end: " + endTimeNano + ", reached: " + (current > endTimeNano));
    return current > endTimeNano;
  }

  public void sleep(long milliseconds) {
    if (isTimeoutReached()) return;

    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }
}
