package com.codeborne.selenide.ex;

import javax.annotation.Nullable;

import static com.codeborne.selenide.ex.Strings.join;

class ObjectConditionError extends UIAssertionError {
  protected ObjectConditionError(String message, String expectedValue, String actualValue,
                                 @Nullable Exception cause) {
    super(
      join(message, errorFormatter.formatActualValue(actualValue)),
      expectedValue,
      actualValue,
      cause
    );
  }
}
