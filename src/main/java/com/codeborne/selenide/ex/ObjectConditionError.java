package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

import static com.codeborne.selenide.ex.Strings.join;

class ObjectConditionError extends UIAssertionError {
  protected ObjectConditionError(Driver driver, String message, String expectedValue, String actualValue) {
    super(
      driver,
      join(message, errorFormatter.formatActualValue(actualValue)),
      expectedValue,
      actualValue
    );
  }
}
