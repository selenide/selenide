package com.codeborne.selenide.ex;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FrameNotFoundError extends UIAssertionError {
  public FrameNotFoundError(String message, Throwable cause) {
    super(message, cause);
  }
}
