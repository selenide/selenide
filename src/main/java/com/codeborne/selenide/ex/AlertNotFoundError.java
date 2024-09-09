package com.codeborne.selenide.ex;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AlertNotFoundError extends UIAssertionError {
  public AlertNotFoundError(Throwable cause) {
    super("Alert not found", cause);
  }
}
