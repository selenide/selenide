package com.codeborne.selenide.ex;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FrameNotFoundException extends UIAssertionError {
  public FrameNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
