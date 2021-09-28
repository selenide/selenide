package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.Cleanup;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class InvalidStateException extends UIAssertionError {
  public InvalidStateException(Throwable cause) {
    super("Invalid element state: " + Cleanup.of.webdriverExceptionMessage(cause.getMessage()), cause);
  }

  public InvalidStateException(String message) {
    super("Invalid element state: " + message);
  }
}
