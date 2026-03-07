package com.codeborne.selenide.impl;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static com.codeborne.selenide.impl.DurationFormat.formatMs;
import static java.lang.System.nanoTime;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.NANOSECONDS;

class CommandGuard {
  private static final Logger log = LoggerFactory.getLogger(CommandGuard.class);
  private static final AtomicLong id = new AtomicLong();

  @Nullable
  static <T> T executeWithTimeout(long timeoutMs, Supplier<@Nullable T> command) {
    final AtomicBoolean completed = new AtomicBoolean(false);
    final Thread worker = currentThread();

    Thread guard = new Thread(() -> {
      long start = nanoTime();
      try {
        sleep(timeoutMs);
        if (!completed.get()) {
          log.info("Command hasn't been completed in {} (timeout: {}) - let's interrupt its thread {}.",
            formatMs(elapsedTimeMs(start)), formatMs(timeoutMs), worker);
          worker.interrupt();
          log.info("Command thread interrupted: {}", worker);
        }
        else if (log.isDebugEnabled()) {
          log.debug("Command has been completed in less than {} (timeout: {})", formatMs(elapsedTimeMs(start)), formatMs(timeoutMs));
        }
      }
      catch (InterruptedException e) {
        if (log.isDebugEnabled()) {
          log.debug("All good, guard interrupted after {} (timeout: {}): {}",
            formatMs(elapsedTimeMs(start)), formatMs(timeoutMs), e.toString());
        }
        currentThread().interrupt();
      }
    });
    guard.setDaemon(true);
    guard.setName("Command guard #%s (timeout: %s)".formatted(id.incrementAndGet(), formatMs(timeoutMs)));
    guard.start();

    try {
      return command.get();
    }
    finally {
      completed.set(true);
      guard.interrupt();
    }
  }

  private static long elapsedTimeMs(long start) {
    return NANOSECONDS.toMillis(nanoTime() - start);
  }

  @SuppressWarnings("BusyWait")
  static void sleep(long durationMs) throws InterruptedException {
    long duration = TimeUnit.MILLISECONDS.toNanos(durationMs);
    long start = nanoTime();
    while (true) {
      long elapsedTime = nanoTime() - start;
      if (elapsedTime >= duration) break;

      long timeToSleep = Math.max(1L, NANOSECONDS.toMillis(duration - elapsedTime));
      Thread.sleep(timeToSleep);
    }
  }
}
