package com.codeborne.selenide.ex;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ObjectCondition;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConditionMetError extends ObjectConditionError {
  public <T> ConditionMetError(Driver driver, ObjectCondition<T> condition, T subject,
                               @Nullable CheckResult checkResult, @Nullable Exception cause) {
    super(
      driver,
      condition.describe(subject) + " " + condition.negativeDescription(),
      condition.expectedValue(),
      checkResult == null ? null : checkResult.getActualValue(),
      cause
    );
  }
}
