package com.codeborne.selenide;


import com.codeborne.selenide.impl.Waiter;

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
    new Waiter().wait(this.driver, this, predicate, message);
  }

  @Override
  public void should(Predicate<LocalStorage> predicate, String message, Duration timeout) {
    new Waiter().wait(this.driver, this, predicate, timeout, message);
  }

  @Override
  public void should(Predicate<LocalStorage> predicate, String message, Duration timeout, Duration polling) {
    new Waiter().wait(this.driver, this, predicate, timeout, polling, message);
  }
}
