package com.codeborne.selenide.ex;

import javax.annotation.Nullable;

public class FileNotDownloadedError extends UIAssertionError {
  public FileNotDownloadedError(String message, long timeout) {
    super(message, timeout, null);
  }

  public FileNotDownloadedError(String message, long timeout, @Nullable Exception cause) {
    super(message, timeout, cause);
  }
}
