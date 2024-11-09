package com.codeborne.selenide.ex;

public class AlertNotFoundError extends UIAssertionError {
  public AlertNotFoundError(Throwable cause) {
    super("Alert not found", cause);
  }
}
