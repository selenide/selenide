package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

public class AlertNotFoundException extends UIAssertionError {
  public AlertNotFoundException(Driver driver, String message, Throwable cause) {
    super(driver, message, cause);
  }
}
