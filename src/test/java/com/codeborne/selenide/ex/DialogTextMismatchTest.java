package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class DialogTextMismatchTest {
  @Test
  void dialogMismatchTextStringTest() {
    Driver driver = new DriverStub();
    DialogTextMismatch dialogTextMismatch = new DialogTextMismatch(driver, "Actual text", "Expected text");

    assertThat(dialogTextMismatch).hasMessage(String.format("Dialog text mismatch%n" +
      "Actual: Actual text%n" +
      "Expected: Expected text%n" +
      "Timeout: 0 ms."));
  }
}
