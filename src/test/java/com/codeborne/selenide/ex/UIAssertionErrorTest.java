package com.codeborne.selenide.ex;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class UIAssertionErrorTest {
  @Test
  void errorMessage() {
    UIAssertionError uiAssertionError = new UIAssertionError("Some it happened", new Throwable("Error message"));

    assertThat(uiAssertionError).hasMessage(String.format("Some it happened%n" +
      "Caused by: java.lang.Throwable: Error message"));
  }
}
