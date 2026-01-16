package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.ModalOptions.withExpectedText;
import static com.codeborne.selenide.ModalOptions.withTimeout;
import static org.assertj.core.api.Assertions.assertThat;

class ModalOptionsTest {
  @Test
  void toString_none() {
    assertThat(ModalOptions.none()).hasToString("");
  }

  @Test
  void toString_withText() {
    assertThat(withExpectedText("Are you sure?"))
      .hasToString("expected text: \"Are you sure?\"");
  }

  @Test
  void toString_withTimeout() {
    assertThat(withTimeout(Duration.ofMillis(1234)))
      .hasToString("timeout: 1.234s");
  }

  @Test
  void toString_withTextAndTimeout() {
    assertThat(withExpectedText("Are you sure?").timeout(Duration.ofMillis(1234)))
      .hasToString("expected text: \"Are you sure?\", timeout: 1.234s");
  }
}
