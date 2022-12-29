package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.impl.Cleanup;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class InvalidStateException extends UIAssertionError {
  public InvalidStateException(Driver driver, String elementDescription, Throwable cause) {
    super(driver, "Invalid element state [" + elementDescription + "]: " +
      Cleanup.of.webdriverExceptionMessage(cause.getMessage()), cause);
  }

  public InvalidStateException(Driver driver, String elementDescription, String message) {
    super(driver, "Invalid element state [" + elementDescription + "]: " + message);
  }
}
