package com.codeborne.selenide.impl;

import org.openqa.selenium.InvalidSelectorException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.DOTALL;

@ParametersAreNonnullByDefault
public class Cleanup {
  private static final Pattern REGEX_FIRST_LINE = Pattern.compile("([^\\n]*)\\n.*", DOTALL);
  private static final Pattern REGEX_SELENIUM_WARNING = Pattern.compile("(.*)\\(WARNING: The server did not provide any stacktrace.*");
  private static final Pattern REGEX_SELENIUM_PACKAGE = Pattern.compile("org\\.openqa\\.selenium\\.(.*)");
  public static Cleanup of = new Cleanup();

  @CheckReturnValue
  @Nonnull
  public String webdriverExceptionMessage(Throwable webDriverException) {
    return requireNonNull(webdriverExceptionMessage(webDriverException.toString()));
  }

  @CheckReturnValue
  @Nullable
  public String webdriverExceptionMessage(@Nullable String webDriverExceptionInfo) {
    if (webDriverExceptionInfo == null) return null;

    return cleanupSeleniumPackage(cleanupSeleniumWarning(extractFirstLine(webDriverExceptionInfo))).trim();
  }

  private String extractFirstLine(String text) {
    return REGEX_FIRST_LINE.matcher(text).replaceFirst("$1");
  }

  private String cleanupSeleniumWarning(String firstLine) {
    return REGEX_SELENIUM_WARNING.matcher(firstLine).replaceFirst("$1");
  }

  private String cleanupSeleniumPackage(String withoutSeleniumBloat) {
    return REGEX_SELENIUM_PACKAGE.matcher(withoutSeleniumBloat).replaceFirst("$1");
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
