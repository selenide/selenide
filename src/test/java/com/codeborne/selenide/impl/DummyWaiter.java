package com.codeborne.selenide.impl;

import javax.annotation.CheckReturnValue;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class DummyWaiter extends Waiter {
  @Override
  @CheckReturnValue
  public <T> void wait(T subject, Predicate<T> condition, long timeout, long pollingInterval) {
    condition.test(subject);
  }
}
