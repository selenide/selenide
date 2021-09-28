package com.codeborne.selenide;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.codeborne.selenide.CheckResult.Verdict.ACCEPT;
import static com.codeborne.selenide.CheckResult.Verdict.REJECT;

/**
 * @since 6.0.0
 */
public class CheckResult implements Serializable {
  private static final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");
  public final Verdict verdict;
  public final Object actualValue;
  public final LocalDateTime timestamp;

  public CheckResult(Verdict verdict, @Nullable Object actualValue, LocalDateTime timestamp) {
    this.verdict = verdict;
    this.actualValue = actualValue;
    this.timestamp = timestamp;
  }

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

  @Override
  public boolean equals(Object object) {
    return object == this || object instanceof CheckResult && equals((CheckResult) object);
  }

  private boolean equals(CheckResult that) {
    return this.verdict == that.verdict && Objects.equals(this.actualValue, that.actualValue);
  }

  @Override
  public int hashCode() {
    return 31 * verdict.hashCode() + (actualValue == null ? 0 : actualValue.hashCode());
  }
}
