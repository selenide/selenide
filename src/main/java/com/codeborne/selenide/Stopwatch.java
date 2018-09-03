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

  public void sleep(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }
}
