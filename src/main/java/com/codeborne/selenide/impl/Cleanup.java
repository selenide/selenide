package com.codeborne.selenide.impl;

import org.openqa.selenium.InvalidSelectorException;

public class Cleanup {
  public static Cleanup of = new Cleanup();

  public String webdriverExceptionMessage(Exception webDriverException) {
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
    return (error instanceof InvalidSelectorException) ||
        error.getMessage().contains("invalid or illegal string was specified") ||
        error.getMessage().contains("nvalid selector") ||
        error.getMessage().contains("is not a valid selector") ||
        error.getMessage().contains("SYNTAX_ERR") ||
        error.getMessage().contains("INVALID_EXPRESSION_ERR") ||
        error.getCause() != null && error.getCause() != error && isInvalidSelectorError(error.getCause());
  }

  public InvalidSelectorException wrap(Exception error) {
    return (error instanceof InvalidSelectorException) ?
        (InvalidSelectorException) error :
        new InvalidSelectorException("Invalid selector", error);
  }
}
