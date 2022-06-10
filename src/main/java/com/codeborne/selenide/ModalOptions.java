package com.codeborne.selenide;

import com.codeborne.selenide.impl.HasTimeout;
import com.github.bsideup.jabel.Desugar;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.time.Duration;

@Desugar
@ParametersAreNonnullByDefault
public record ModalOptions(
  @Nullable String expectedText,
  @Nullable Duration timeout
) implements HasTimeout {

  @CheckReturnValue
  @Nonnull
  public static ModalOptions none() {
    return new ModalOptions(null, null);
  }

  @CheckReturnValue
  @Nonnull
  public static ModalOptions withExpectedText(@Nullable String expectedText) {
    return new ModalOptions(expectedText, null);
  }

  @CheckReturnValue
  @Nonnull
  public static ModalOptions withTimeout(Duration timeout) {
    return new ModalOptions(null, timeout);
  }

  @CheckReturnValue
  @Nonnull
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
