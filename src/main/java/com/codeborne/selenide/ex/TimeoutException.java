package com.codeborne.selenide.ex;

import javax.annotation.ParametersAreNonnullByDefault;
import java.net.SocketTimeoutException;

@ParametersAreNonnullByDefault
public class TimeoutException extends RuntimeException {
  public TimeoutException(String message, SocketTimeoutException cause) {
    super(message, cause);
  }
}
