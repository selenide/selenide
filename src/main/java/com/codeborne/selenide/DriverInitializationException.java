package com.codeborne.selenide;

public class DriverInitializationException extends RuntimeException {

  public DriverInitializationException() {
  }

  public DriverInitializationException(String message) {
    super(message);
  }

  public DriverInitializationException(String message, Throwable cause) {
    super(message, cause);
  }

  public DriverInitializationException(Throwable cause) {
    super(cause);
  }
}
