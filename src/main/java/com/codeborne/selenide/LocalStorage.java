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
    new ConditionWaiter(driver).waitFor(this, predicate, message);
  }

  @Override
  public void should(Predicate<LocalStorage> predicate, String message, Duration timeout) {
    new ConditionWaiter(driver).waitFor(this, predicate, message, timeout);
  }

  @Override
  public void should(Predicate<LocalStorage> predicate, String message, Duration timeout, Duration polling) {
    new ConditionWaiter(driver).waitFor(this, predicate, message, timeout, polling);
  }
}
