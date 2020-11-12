package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Cleanup;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class InvalidStateException extends UIAssertionError {
  public InvalidStateException(Driver driver, Throwable cause) {
    super(driver, "Invalid element state: " + Cleanup.of.webdriverExceptionMessage(cause.getMessage()), cause);
  }

  public InvalidStateException(Driver driver, String message) {
    super(driver, "Invalid element state: " + message);
  }
}
