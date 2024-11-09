package com.codeborne.selenide.ex;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;
import org.jspecify.annotations.Nullable;

public class ConditionMetError extends ObjectConditionError {
  public <T> ConditionMetError(ObjectCondition<T> condition, T subject,
                               @Nullable CheckResult checkResult, @Nullable Exception cause) {
    super(
      condition.describe(subject) + " " + condition.negativeDescription(),
      condition.expectedValue(),
      checkResult == null ? null : checkResult.getActualValue(),
      cause
    );
  }
}
