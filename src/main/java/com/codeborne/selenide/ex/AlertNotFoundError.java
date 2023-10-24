package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AlertNotFoundError extends UIAssertionError {
  public AlertNotFoundError(Driver driver, Throwable cause) {
    super(driver, "Alert not found", cause);
  }
}
