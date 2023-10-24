package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class WindowNotFoundError extends UIAssertionError {
  public WindowNotFoundError(Driver driver, String message, Throwable cause) {
    super(driver, message, cause);
  }
}
