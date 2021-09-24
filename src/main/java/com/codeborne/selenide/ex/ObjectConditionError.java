package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

import static com.codeborne.selenide.ex.ErrorMessages.formatActualValue;

class ObjectConditionError extends UIAssertionError {
  protected <T> ObjectConditionError(Driver driver, String message, String expectedValue, String actualValue) {
    super(driver,
      message + formatActualValue(actualValue),
      expectedValue,
      actualValue
    );
  }
}
