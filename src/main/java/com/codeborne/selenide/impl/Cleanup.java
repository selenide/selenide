package com.codeborne.selenide.impl;

import org.openqa.selenium.InvalidSelectorException;

public class Cleanup {
  public static Cleanup of = new Cleanup();

  public String webdriverExceptionMessage(Throwable webDriverException) {
    return webdriverExceptionMessage(webDriverException.toString());
  }

  protected String webdriverExceptionMessage(String webDriverExceptionInfo) {
    return webDriverExceptionInfo == null || webDriverExceptionInfo.indexOf('\n') == -1 ?
      webDriverExceptionInfo :
      webDriverExceptionInfo
        .substring(0, webDriverExceptionInfo.indexOf('\n'))
        .replaceFirst("(.*)\\(WARNING: The server did not provide any stacktrace.*", "$1")
        .replaceFirst("org\\.openqa\\.selenium\\.(.*)", "$1")
        .trim();
  }

  public boolean isInvalidSelectorError(Throwable error) {
    if (error == null) return false;

    String message = error.getMessage();
    if (message == null) return false;

    return (error instanceof InvalidSelectorException && !message.contains("\"Element is not selectable\"")) ||
      isInvalidSelectorMessage(message) ||
      error.getCause() != null && error.getCause() != error && isInvalidSelectorError(error.getCause());
  }

  private boolean isInvalidSelectorMessage(String message) {
    return message.contains("invalid or illegal string was specified") ||
      message.contains("Invalid selector") ||
      message.contains("invalid selector") ||
      message.contains("is not a valid selector") ||
      message.contains("SYNTAX_ERR") ||
      message.contains("INVALID_EXPRESSION_ERR");
  }

  public InvalidSelectorException wrap(Throwable error) {
    return (error instanceof InvalidSelectorException) ?
      (InvalidSelectorException) error :
      new InvalidSelectorException("Invalid selector", error);
  }
}
