package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ObjectCondition;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConditionMetException extends ObjectConditionError {
  public <T> ConditionMetException(Driver driver, ObjectCondition<T> condition, T subject) {
    super(
      driver,
      condition.describe(subject) + " " + condition.negativeDescription(),
      condition.expectedValue(),
      errorFormatter.extractActualValue(condition, subject)
    );
  }
}
