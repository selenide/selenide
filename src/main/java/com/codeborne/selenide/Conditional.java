package com.codeborne.selenide;

import com.codeborne.selenide.impl.Waiter;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.time.Duration;

/**
 * <p>
 * Implements wait for non WebElement based conditions.
 * Accepts predicate as condition.
 * </p>
 *
 * <p>
 * To use it just implement for appropriate type,
 * e.g. {@code YourType implements Conditional<YourType>}
 * </p>
 *
 * {@link com.codeborne.selenide.impl.Waiter}
 */
public interface Conditional<T> {
  /**
   * @return current Driver (used to take screenshots etc. in case of test failure)
   */
  Driver driver();

  /**
   * @return object under test
   */
  T object();

  @CanIgnoreReturnValue
  default Conditional<T> shouldHave(ObjectCondition<T> predicate) {
    new Waiter().wait(driver(), object(), predicate);
    return this;
  }

  @CanIgnoreReturnValue
  default Conditional<T> shouldHave(ObjectCondition<T> predicate, Duration timeout) {
    new Waiter().wait(driver(), object(), predicate, timeout);
    return this;
  }

  @CanIgnoreReturnValue
  default Conditional<T> shouldNotHave(ObjectCondition<T> predicate) {
    new Waiter().waitWhile(driver(), object(), predicate);
    return this;
  }

  @CanIgnoreReturnValue
  default Conditional<T> shouldNotHave(ObjectCondition<T> predicate, Duration timeout) {
    new Waiter().waitWhile(driver(), object(), predicate, timeout);
    return this;
  }
}
