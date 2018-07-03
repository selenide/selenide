package com.codeborne.selenide.ex;

import java.net.SocketTimeoutException;

public class TimeoutException extends RuntimeException {
  public TimeoutException(String message, SocketTimeoutException cause) {
    super(message, cause);
  }
}
