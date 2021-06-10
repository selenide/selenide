package com.codeborne.selenide.ex;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConditionNotMatchException extends AssertionError {

  public ConditionNotMatchException(String message) {
    super(message);
  }
}
