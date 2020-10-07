package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

final class DialogTextMismatchTest implements WithAssertions {
  @Test
  void dialogMismatchTextStringTest() {
    Driver driver = new DriverStub();
    DialogTextMismatch dialogTextMismatch = new DialogTextMismatch(driver, "Actual text", "Expected text");

    assertThat(dialogTextMismatch).hasMessage(String.format("Dialog text mismatch%n" +
      "Actual: Actual text%n" +
      "Expected: Expected text%n" +
      "Screenshot: null%n" +
      "Timeout: 0 ms."));
  }
}
