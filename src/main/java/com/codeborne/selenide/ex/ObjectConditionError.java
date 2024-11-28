package com.codeborne.selenide.ex;

import org.jspecify.annotations.Nullable;

import static com.codeborne.selenide.ex.Strings.join;

class ObjectConditionError extends UIAssertionError {
  protected ObjectConditionError(String message,
                                 @Nullable String expectedValue, @Nullable String actualValue,
                                 @Nullable Exception cause) {
    super(
      join(message, errorFormatter.formatActualValue(actualValue)),
      expectedValue,
      actualValue,
      cause
    );
  }
}
