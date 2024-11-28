package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.Cleanup;

public class InvalidStateError extends UIAssertionError {
  public InvalidStateError(String elementDescription, Throwable cause) {
    super("Invalid element state [" + elementDescription + "]: " +
      Cleanup.of.webdriverExceptionMessage(cause.getMessage()), cause);
  }

  public InvalidStateError(String elementDescription, String message) {
    super("Invalid element state [" + elementDescription + "]: " + message);
  }
}
