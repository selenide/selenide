package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class FrameNotFoundException extends UIAssertionError {
  public FrameNotFoundException(Driver driver, String message, Throwable cause) {
    super(driver, message, cause);
  }
}
