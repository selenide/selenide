package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

public class InvalidStateException extends UIAssertionError {
  public InvalidStateException(Driver driver, Throwable cause) {
    super(driver, cause);
  }

  public InvalidStateException(Driver driver, String message) {
    super(driver, message);
  }

  @Override
  public String toString() {
    return (getCause() != null ? getCause().toString() : super.toString()) + uiDetails();
  }
}
