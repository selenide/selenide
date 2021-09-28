package com.codeborne.selenide.ex;

import static com.codeborne.selenide.ex.ErrorMessages.formatActualValue;

class ObjectConditionError extends UIAssertionError {
  protected ObjectConditionError(String message, String expectedValue, String actualValue) {
    super(
      message + formatActualValue(actualValue),
      expectedValue,
      actualValue
    );
  }
}
