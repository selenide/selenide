package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ObjectCondition;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ex.ErrorMessages.actualValue;

@ParametersAreNonnullByDefault
public class ConditionNotMetException extends UIAssertionError {
  public <T> ConditionNotMetException(Driver driver, ObjectCondition<T> condition, T subject) {
    super(driver, condition.describe(subject) + " " + condition.description() + actualValue(condition, subject));
  }
}
