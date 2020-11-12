package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class WindowNotFoundException extends UIAssertionError {
  public WindowNotFoundException(Driver driver, String message, Throwable cause) {
    super(driver, message, cause);
  }
}
