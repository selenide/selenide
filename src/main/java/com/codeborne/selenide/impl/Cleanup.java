package com.codeborne.selenide.impl;

import org.openqa.selenium.InvalidSelectorException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class Cleanup {
  public static Cleanup of = new Cleanup();

  @CheckReturnValue
  @Nonnull
  public String webdriverExceptionMessage(Throwable webDriverException) {
    return requireNonNull(webdriverExceptionMessage(webDriverException.toString()));
  }

  @CheckReturnValue
  @Nullable
  public String webdriverExceptionMessage(@Nullable String webDriverExceptionInfo) {
    return webDriverExceptionInfo == null || webDriverExceptionInfo.indexOf('\n') == -1 ?
      webDriverExceptionInfo :
      webDriverExceptionInfo
        .substring(0, webDriverExceptionInfo.indexOf('\n'))
        .replaceFirst("(.*)\\(WARNING: The server did not provide any stacktrace.*", "$1")
        .replaceFirst("org\\.openqa\\.selenium\\.(.*)", "$1")
        .trim();
  }

  public boolean isInvalidSelectorError(@Nullable Throwable error) {
    if (error == null) return false;
    if (error instanceof AssertionError) return false;

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
