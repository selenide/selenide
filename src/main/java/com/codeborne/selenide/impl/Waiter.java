package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ObjectCondition;
import com.codeborne.selenide.ex.ConditionMetException;
import com.codeborne.selenide.ex.ConditionNotMetException;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.logevents.SelenideLog;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.function.Supplier;

import static com.codeborne.selenide.logevents.LogEvent.EventStatus.PASS;
import static java.lang.System.currentTimeMillis;

@ParametersAreNonnullByDefault
public class Waiter {
  private static final Logger logger = LoggerFactory.getLogger(Waiter.class);

  @CheckReturnValue
  public void wait(long timeout, long pollingInterval, Supplier<Boolean> condition) {
    sleep(pollingInterval);
    for (long start = currentTimeMillis();
         !isTimeoutExceeded(timeout, start) && !condition.get(); ) {
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
    SelenideLog log = SelenideLogger.beginStep(condition.describe(subject), condition.description());
    for (long start = currentTimeMillis(); !isTimeoutExceeded(timeout, start); ) {
      if (checkUnThrowable(subject, condition)) {
        SelenideLogger.commitStep(log, PASS);
        return;
      }
      sleep(pollingInterval);
    }

    Error failure = UIAssertionError.wrap(driver, new ConditionNotMetException(driver, condition, subject), timeout);
    SelenideLogger.commitStep(log, failure);
    throw failure;
  }

  public <T> void waitWhile(Driver driver, T subject, ObjectCondition<T> condition) {
    waitWhile(driver, subject, condition, driver.config().timeout(), driver.config().pollingInterval());
  }

  public <T> void waitWhile(Driver driver, T subject, ObjectCondition<T> condition, Duration timeout) {
    waitWhile(driver, subject, condition, timeout.toMillis(), driver.config().pollingInterval());
  }

  private <T> void waitWhile(Driver driver, T subject, ObjectCondition<T> condition, long timeout, long pollingInterval) {
    SelenideLog log = SelenideLogger.beginStep(subject.toString(), condition.negativeDescription());
    for (long start = currentTimeMillis(); !isTimeoutExceeded(timeout, start); ) {
      if (!checkUnThrowable(subject, condition)) {
        SelenideLogger.commitStep(log, PASS);
        return;
      }
      sleep(pollingInterval);
    }

    Error failure = UIAssertionError.wrap(driver, new ConditionMetException(driver, condition, subject), timeout);
    SelenideLogger.commitStep(log, failure);
    throw failure;
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
