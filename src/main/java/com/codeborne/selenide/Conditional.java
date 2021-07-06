package com.codeborne.selenide;

import com.codeborne.selenide.impl.Waiter;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;
import java.util.function.Predicate;

/**
 * <p>
 * Implements wait for non WebElement based conditions.
 * Accepts predicate as condition.
 * </p>
 *
 * <p>
 * To use it just implement for appropriate type,
 * e.g. 'YourType implements Conditional<YourType>'
 * </p>
 *
 * @link com.codeborne.selenide.impl.Waiter
 * @since 5.23.0
 */
@ParametersAreNonnullByDefault
public interface Conditional<T> {
  /**
   * @return current Driver (used to take screenshots etc. in case of test failure)
   */
  @Nonnull
  @CheckReturnValue
  Driver driver();

  /**
   * @return object under test
   */
  @Nonnull
  @CheckReturnValue
  T object();

  default Conditional<T> shouldHave(Predicate<T> predicate, String message) {
    new Waiter().wait(driver(), object(), predicate, message);
    return this;
  }

  default Conditional<T> shouldHave(Predicate<T> predicate, String message, Duration timeout) {
    new Waiter().wait(driver(), object(), predicate, timeout, message);
    return this;
  }

  default Conditional<T> shouldNotHave(Predicate<T> predicate, String message) {
    new Waiter().wait(driver(), object(), predicate.negate(), message);
    return this;
  }

  default Conditional<T> shouldNotHave(Predicate<T> predicate, String message, Duration timeout) {
    new Waiter().wait(driver(), object(), predicate.negate(), timeout, message);
    return this;
  }
}
