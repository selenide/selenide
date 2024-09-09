package com.codeborne.selenide.ex;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.ObjectCondition;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConditionNotMetError extends ObjectConditionError {
  public <T> ConditionNotMetError(ObjectCondition<T> condition, T subject,
                                  @Nullable CheckResult checkResult, @Nullable Exception cause) {
    super(
      condition.describe(subject) + " " + condition.description(),
      condition.expectedValue(),
      checkResult == null ? null : checkResult.getActualValue(),
      cause
    );
  }
}
