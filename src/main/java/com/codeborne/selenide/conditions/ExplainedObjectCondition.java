package com.codeborne.selenide.conditions;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;
import org.jspecify.annotations.Nullable;

public class ExplainedObjectCondition<T> implements ObjectCondition<T> {
  private final ObjectCondition<T> delegate;
  private final String message;

  public ExplainedObjectCondition(ObjectCondition<T> delegate, String message) {
    this.delegate = delegate;
    this.message = message;
  }

  @Override
  public String description() {
    return delegate.description() + " (because " + message + ")";
  }

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

  @Override
  public String describe(T object) {
    return delegate.describe(object);
  }

}
