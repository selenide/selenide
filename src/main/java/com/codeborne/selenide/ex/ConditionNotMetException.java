package com.codeborne.selenide.ex;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConditionNotMetException extends AssertionError {

  public ConditionNotMetException(String message) {
    super(message);
  }
}
