package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ObjectCondition;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConditionNotMetException extends ObjectConditionError {
  public <T> ConditionNotMetException(Driver driver, ObjectCondition<T> condition, T subject) {
    super(
      driver,
      condition.describe(subject) + " " + condition.description(),
      condition.expectedValue(),
      errorFormatter.extractActualValue(condition, subject)
    );
  }
}
