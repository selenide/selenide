package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ObjectCondition;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ex.ErrorMessages.actualValue;

@ParametersAreNonnullByDefault
public class ConditionMetException extends UIAssertionError {
  public <T> ConditionMetException(Driver driver, ObjectCondition<T> condition, T subject) {
    super(driver, condition.describe(subject) + " " + condition.negativeDescription() + actualValue(condition, subject));
  }
}
