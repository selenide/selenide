package com.codeborne.selenide.impl;

import static java.lang.System.currentTimeMillis;

import java.util.function.Predicate;

public class Waiter {
  public <T> void wait(T subject, Predicate<T> condition, long timeout, long pollingInterval) {
    for (long start = currentTimeMillis();
         !isTimeoutExceeded(timeout, start) && !condition.test(subject); ) {
      sleep(pollingInterval);
    }
  }

  private boolean isTimeoutExceeded(long timeout, long start) {
    return currentTimeMillis() - start > timeout;
  }

  void sleep(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }
}
