package com.codeborne.selenide.ex;

import com.codeborne.selenide.ObjectCondition;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ex.ErrorMessages.extractActualValue;

@ParametersAreNonnullByDefault
public class ConditionNotMetException extends ObjectConditionError {
  public <T> ConditionNotMetException(ObjectCondition<T> condition, T subject) {
    super(
      condition.describe(subject) + " " + condition.description(),
      condition.expectedValue(),
      extractActualValue(condition, subject)
    );
  }
}
