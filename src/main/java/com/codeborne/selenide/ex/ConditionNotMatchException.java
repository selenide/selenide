package com.codeborne.selenide.ex;

import javax.annotation.ParametersAreNonnullByDefault;

import static java.lang.String.format;

@ParametersAreNonnullByDefault
public class ConditionNotMatchException extends AssertionError {

  public ConditionNotMatchException(String message) {
    super(format("Condition doesn't match: %s", message));
  }
}
