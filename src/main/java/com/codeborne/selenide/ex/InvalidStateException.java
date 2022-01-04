package com.codeborne.selenide.ex;

import com.codeborne.selenide.impl.Cleanup;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class InvalidStateException extends UIAssertionError {
  public InvalidStateException(String elementDescription, Throwable cause) {
    super("Invalid element state [" + elementDescription + "]: " +
      Cleanup.of.webdriverExceptionMessage(cause.getMessage()), cause);
  }

  public InvalidStateException(String elementDescription, String message) {
    super("Invalid element state [" + elementDescription + "]: " + message);
  }
}
