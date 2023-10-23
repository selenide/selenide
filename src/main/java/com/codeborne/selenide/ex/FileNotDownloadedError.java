package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;

import javax.annotation.Nullable;

public class FileNotDownloadedError extends UIAssertionError {
  public FileNotDownloadedError(Driver driver, String message, long timeout) {
    super(driver, message, timeout, null);
  }

  public FileNotDownloadedError(Driver driver, String message, long timeout, @Nullable Exception cause) {
    super(driver, message, timeout, cause);
  }
}
