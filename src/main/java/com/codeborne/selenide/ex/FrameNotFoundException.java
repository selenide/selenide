package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

public class FrameNotFoundException extends UIAssertionError {
  public FrameNotFoundException(Driver driver, String message, Throwable cause) {
    super(driver, message, cause);
  }
}
