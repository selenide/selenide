package com.codeborne.selenide.ex;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class WindowNotFoundException extends UIAssertionError {
  public WindowNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
