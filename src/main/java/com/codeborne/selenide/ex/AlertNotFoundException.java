package com.codeborne.selenide.ex;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AlertNotFoundException extends UIAssertionError {
  public AlertNotFoundException(Throwable cause) {
    super("Alert not found", cause);
  }
}
