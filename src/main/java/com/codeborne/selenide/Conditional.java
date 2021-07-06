package com.codeborne.selenide;

import java.time.Duration;
import java.util.function.Predicate;

/**
 * Implements wait for non WebElement based conditions.
 * Accepts predicate as condition.
 *
 * To use it just implement for appropriate type,
 * e.g. 'YourType implements Conditional<YourType>'
 *
 * For implementing wait with polling use ConditionWaiter
 * @link com.codeborne.selenide.ConditionWaiter
 * @since 5.23.0
 */
public interface Conditional<T> {

  void should(Predicate<T> predicate, String message);
  void shouldNot(Predicate<T> predicate, String message);

  void should(Predicate<T> predicate, String message, Duration timeout);
  void shouldNot(Predicate<T> predicate, String message, Duration timeout);
}
