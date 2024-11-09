package com.codeborne.selenide;

import com.codeborne.selenide.impl.HasTimeout;
import org.jspecify.annotations.Nullable;

import java.time.Duration;

public record ModalOptions(
  @Nullable String expectedText,
  @Nullable Duration timeout
) implements HasTimeout {

  public static ModalOptions none() {
    return new ModalOptions(null, null);
  }

  public static ModalOptions withExpectedText(@Nullable String expectedText) {
    return new ModalOptions(expectedText, null);
  }

  public static ModalOptions withTimeout(Duration timeout) {
    return new ModalOptions(null, timeout);
  }

  public ModalOptions timeout(Duration timeout) {
    return new ModalOptions(expectedText, timeout);
  }

  @Override
  public String toString() {
    if (expectedText == null && timeout == null)
      return "";
    else if (expectedText == null)
      return String.format("timeout: %s", timeout);
    else if (timeout == null)
      return String.format("expected text: \"%s\"", expectedText);
    else
      return String.format("expected text: \"%s\", timeout: %s ms", expectedText, timeout.toMillis());
  }
}
