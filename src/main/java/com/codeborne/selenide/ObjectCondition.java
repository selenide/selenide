package com.codeborne.selenide;

import com.codeborne.selenide.conditions.ExplainedObjectCondition;
import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

public interface ObjectCondition<T> {
  String description();

  String negativeDescription();

  CheckResult check(T object);

  @Nullable
  String expectedValue();

  String describe(T object);

  default String message(T object) {
    return describe(object) + " " + description();
  }

  default CheckResult result(T object, boolean met, @Nullable Object actualValue) {
    return met ? accepted(actualValue) : rejected(message(object), actualValue);
  }

  default ObjectCondition<T> because(String message) {
    return new ExplainedObjectCondition<>(this, message);
  }

}
