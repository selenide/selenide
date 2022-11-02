package com.codeborne.selenide.impl;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public class DummyWaiter extends Waiter {
  @Override
  public void wait(long timeout, long pollingInterval, Supplier<Boolean> condition) {
    condition.get();
  }
}
