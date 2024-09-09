package com.codeborne.selenide.ex;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class DialogTextMismatchTest {
  @Test
  void dialogMismatchTextStringTest() {
    DialogTextMismatch dialogTextMismatch = new DialogTextMismatch("Expected text", "Actual text");

    assertThat(dialogTextMismatch).hasMessage(String.format("Dialog text mismatch%n" +
      "Actual: Actual text%n" +
      "Expected: Expected text"));
  }
}
