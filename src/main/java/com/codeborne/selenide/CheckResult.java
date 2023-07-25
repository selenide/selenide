package com.codeborne.selenide;

import com.github.bsideup.jabel.Desugar;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;
import static java.util.Objects.requireNonNull;

/**
 * @since 6.0.0
 */
@Desugar
@ParametersAreNonnullByDefault
public record CheckResult(
  Verdict verdict,
  @Nullable String message,
  @Nullable Object actualValue,
  LocalDateTime timestamp
) {
  private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

  public static CheckResult rejected(String message, Object actualValue) {
    return new CheckResult(REJECT, message, actualValue, LocalDateTime.now());
  }

  public static CheckResult accepted() {
    return new CheckResult(ACCEPT, null);
  }

  public CheckResult(Verdict verdict, @Nullable Object actualValue) {
    this(verdict, null, actualValue, LocalDateTime.now());
  }

  public CheckResult(boolean checkSucceeded, @Nullable Object actualValue) {
    this(checkSucceeded ? ACCEPT : REJECT, actualValue);
  }

  public enum Verdict {
    ACCEPT,
    REJECT
  }

  @Override
  public String toString() {
    return String.format("%s @ %s%n", actualValue, timeFormat.format(timestamp));
  }

  @Nonnull
  @CheckReturnValue
  public String getMessageOrElse(Supplier<String> defaultMessage) {
    return message() == null ? defaultMessage.get() : message();
  }

  @Nullable
  @CheckReturnValue
  @SuppressWarnings("unchecked")
  public <T> T getActualValue() {
    return (T) actualValue();
  }

  @Nonnull
  @CheckReturnValue
  @SuppressWarnings("unchecked")
  public <T> T getActualValueOrElse(T defaultValue) {
    return actualValue() == null ? defaultValue : (T) actualValue();
  }

  @Nonnull
  @CheckReturnValue
  @SuppressWarnings("unchecked")
  public <T> T requireActualValue() {
    return requireNonNull((T) actualValue());
  }
}
