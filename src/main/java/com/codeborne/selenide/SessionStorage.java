package com.codeborne.selenide;

import com.codeborne.selenide.impl.Waiter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
public class SessionStorage extends JSStorage {
  SessionStorage(Driver driver) {
    super(driver, "sessionStorage");
  }

  public void should(Predicate<SessionStorage> predicate, String message) {
    new Waiter().wait(this.driver, this, predicate, message);
  }

  public void shouldNot(Predicate<SessionStorage> predicate, String message) {
    should(predicate.negate(), message);
  }

  public void should(Predicate<SessionStorage> predicate, String message, Duration timeout) {
    new Waiter().wait(this.driver, this, predicate, timeout, message);
  }

  public void shouldNot(Predicate<SessionStorage> predicate, String message, Duration timeout) {
    should(predicate.negate(), message, timeout);
  }
}
