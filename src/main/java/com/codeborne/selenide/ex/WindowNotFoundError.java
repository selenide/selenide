package com.codeborne.selenide.ex;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class WindowNotFoundError extends UIAssertionError {
  public WindowNotFoundError(String message, Throwable cause) {
    super(message, cause);
  }
}
