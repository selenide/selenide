package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ObjectCondition;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ex.ErrorMessages.extractActualValue;

@ParametersAreNonnullByDefault
public class ConditionMetException extends ObjectConditionError {
  public <T> ConditionMetException(Driver driver, ObjectCondition<T> condition, T subject) {
    super(
      driver,
      condition.describe(subject) + " " + condition.negativeDescription(),
      condition.expectedValue(),
      extractActualValue(condition, subject)
    );
  }
}
