package com.codeborne.selenide;


import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.function.Predicate;


@ParametersAreNonnullByDefault
public class LocalStorage extends JSStorage implements Conditional<LocalStorage> {
  LocalStorage(Driver driver) {
    super(driver, "localStorage");
  }

  @Override
  public void should(Predicate<LocalStorage> predicate, String message) {
    ConditionWaiter.waitFor(this, predicate, message);
  }

  @Override
  public void should(Predicate<LocalStorage> predicate, String message, Duration timeout) {
    ConditionWaiter.waitFor(this, predicate, message, timeout);
  }

  @Override
  public void should(Predicate<LocalStorage> predicate, String message, Duration timeout, Duration polling) {
    ConditionWaiter.waitFor(this, predicate, message, timeout, polling);
  }
}
