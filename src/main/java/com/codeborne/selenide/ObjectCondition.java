package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public interface ObjectCondition<T> {
  @Nonnull
  @CheckReturnValue
  String description();

  @Nonnull
  @CheckReturnValue
  String negativeDescription();

  @CheckReturnValue
  CheckResult check(T object);

  @Nullable
  @CheckReturnValue
  String expectedValue();

  @Nonnull
  @CheckReturnValue
  String describe(T object);

  default String message(T object) {
    return describe(object) + " " + description();
  }

  default CheckResult result(T object, boolean met, @Nullable Object actualValue) {
    return met ? accepted(actualValue) : rejected(message(object), actualValue);
  }
}
