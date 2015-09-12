package com.codeborne.selenide.ex;

public class InvalidStateException extends UIAssertionError {
  public InvalidStateException(Throwable cause) {
    super(cause);
  }

  public InvalidStateException(String message) {
    super(message);
  }

  @Override
  public String toString() {
    return (getCause() != null ? getCause().toString() : super.toString()) + uiDetails();
  }
}
