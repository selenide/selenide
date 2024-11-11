package com.codeborne.selenide.impl;

import java.util.function.Supplier;

public class DummyWaiter extends Waiter {
  @Override
  public void wait(long timeout, long pollingInterval, Supplier<Boolean> condition) {
    condition.get();
  }
}
