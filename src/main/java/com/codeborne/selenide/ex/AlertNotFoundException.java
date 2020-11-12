package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AlertNotFoundException extends UIAssertionError {
  public AlertNotFoundException(Driver driver, String message, Throwable cause) {
    super(driver, message, cause);
  }
}
