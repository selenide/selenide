package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ExplainedObjectCondition<T> implements ObjectCondition<T> {
  private final ObjectCondition<T> delegate;
  private final String message;

  public ExplainedObjectCondition(ObjectCondition<T> delegate, String message) {
    this.delegate = delegate;
    this.message = message;
  }

  @Nonnull
  @Override
  public String description() {
    return delegate.description() + " (because " + message + ")";
  }

  @Nonnull
  @Override
  public String negativeDescription() {
    return delegate.negativeDescription() + " (because " + message + ")";
  }

  @Override
  public CheckResult check(T object) {
    return delegate.check(object);
  }

  @Nullable
  @Override
  public String expectedValue() {
    return delegate.expectedValue();
  }

  @Nonnull
  @Override
  public String describe(T object) {
    return delegate.describe(object);
  }

}
