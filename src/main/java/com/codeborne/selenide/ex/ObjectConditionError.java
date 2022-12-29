package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

class ObjectConditionError extends UIAssertionError {
  protected ObjectConditionError(Driver driver, String message, String expectedValue, String actualValue) {
    super(
      driver,
      message + errorFormatter.formatActualValue(actualValue),
      expectedValue,
      actualValue
    );
  }
}
