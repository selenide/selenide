package com.codeborne.selenide;

import static java.lang.System.nanoTime;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Stopwatch {
  private final long endTimeNano;

  public Stopwatch(long timeoutMs) {
    this.endTimeNano = nanoTime() + MILLISECONDS.toNanos(timeoutMs);
  }

  public boolean isTimeoutReached() {
    return nanoTime() > endTimeNano;
  }
}
