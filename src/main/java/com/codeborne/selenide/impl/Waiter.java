package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ex.ConditionNotMetException;
import com.codeborne.selenide.ex.UIAssertionError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.function.Predicate;

import static java.lang.System.currentTimeMillis;
import static java.time.Duration.ofMillis;

@ParametersAreNonnullByDefault
public class Waiter {
  private static final Logger logger = LoggerFactory.getLogger(Waiter.class);

  @CheckReturnValue
  public <T> void wait(T subject, Predicate<T> condition, long timeout, long pollingInterval) {
    sleep(pollingInterval);
    for (long start = currentTimeMillis();
         !isTimeoutExceeded(timeout, start) && !checkUnThrowable(subject, condition); ) {
      sleep(pollingInterval);
    }
  }

  public <T> void wait(Driver driver, T subject, Predicate<T> condition, String message) {
    wait(driver, subject, condition, ofMillis(driver.config().timeout()), ofMillis(driver.config().pollingInterval()), message);
  }

  public <T> void wait(Driver driver, T subject, Predicate<T> condition, Duration timeout, String message) {
    wait(driver, subject, condition, timeout, ofMillis(driver.config().pollingInterval()), message);
  }

  public <T> void wait(Driver driver, T subject, Predicate<T> condition, Duration timeout, Duration pollingInterval, String message) {
    wait(subject, condition, timeout.toMillis(), pollingInterval.toMillis());
    if (!checkUnThrowable(subject, condition))
      throw UIAssertionError.wrap(driver, new ConditionNotMetException(driver, message), timeout.toMillis());
  }

  private boolean isTimeoutExceeded(long timeout, long start) {
    return currentTimeMillis() - start > timeout;
  }

  private void sleep(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  private <T> boolean checkUnThrowable(T subject, Predicate<T> predicate) {
    try {
      return predicate.test(subject);
    }
    catch (Exception e) {
      logger.info("Fail to check condition", e);
      return false;
    }
  }
}
