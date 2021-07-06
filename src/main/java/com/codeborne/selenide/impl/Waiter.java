package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.ex.ConditionNotMetException;
import com.codeborne.selenide.ex.UIAssertionError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.function.Predicate;

import static java.lang.System.currentTimeMillis;

@ParametersAreNonnullByDefault
public class Waiter {
  private static final Logger logger = LoggerFactory.getLogger(Waiter.class);

  @CheckReturnValue
  public <T> void wait(T subject, Predicate<T> condition, long timeout, long pollingInterval) {
    sleep(pollingInterval);
    for (long start = currentTimeMillis();
         !isTimeoutExceeded(timeout, start) && !condition.test(subject); ) {
      sleep(pollingInterval);
    }
  }

  public <T> void wait(Driver driver, T subject, ObjectCondition<T> condition) {
    wait(driver, subject, condition, driver.config().timeout(), driver.config().pollingInterval());
  }

  public <T> void wait(Driver driver, T subject, ObjectCondition<T> condition, Duration timeout) {
    wait(driver, subject, condition, timeout.toMillis(), driver.config().pollingInterval());
  }

  private <T> void wait(Driver driver, T subject, ObjectCondition<T> condition, long timeout, long pollingInterval) {
    for (long start = currentTimeMillis(); !isTimeoutExceeded(timeout, start); ) {
      if (checkUnThrowable(subject, condition)) {
        return;
      }
      sleep(pollingInterval);
    }

    throw UIAssertionError.wrap(driver, new ConditionNotMetException(driver, condition.description()), timeout);
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

  private <T> boolean checkUnThrowable(T subject, ObjectCondition<T> predicate) {
    try {
      return predicate.test(subject);
    }
    catch (Exception e) {
      logger.info("Fail to check condition", e);
      return false;
    }
  }
}
