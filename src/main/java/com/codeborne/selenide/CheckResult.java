package com.codeborne.selenide;

import com.github.bsideup.jabel.Desugar;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;

/**
 * @since 6.0.0
 */
@Desugar
@ParametersAreNonnullByDefault
public record CheckResult(
  Verdict verdict,
  @Nullable Object actualValue,
  LocalDateTime timestamp
) {
  private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

  public CheckResult(Verdict verdict, @Nullable Object actualValue) {
    this(verdict, actualValue, LocalDateTime.now());
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
}
