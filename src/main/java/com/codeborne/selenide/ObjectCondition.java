package com.codeborne.selenide;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ObjectCondition<T> {
  @Nonnull
  @CheckReturnValue
  String description();

  @Nonnull
  @CheckReturnValue
  String negativeDescription();

  @CheckReturnValue
  boolean test(T object);

  @Nullable
  @CheckReturnValue
  String actualValue(T object);

  @Nullable
  @CheckReturnValue
  String expectedValue();

  @Nonnull
  @CheckReturnValue
  String describe(T object);
}
