package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConditionNotMetException extends UIAssertionError {
  public ConditionNotMetException(Driver driver, String message) {
    super(driver, message);
  }
}
