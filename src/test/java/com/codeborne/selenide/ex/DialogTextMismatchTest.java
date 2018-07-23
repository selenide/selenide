package com.codeborne.selenide.ex;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class DialogTextMismatchTest implements WithAssertions {
  @Test
  void dialogMismatchTextStringTest() {
    DialogTextMismatch dialogTextMismatch = new DialogTextMismatch("Actual text", "Expected text");
    String expectedString = "DialogTextMismatch \n" +
      "Actual: Actual text\n" +
      "Expected: Expected text\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.";
    assertThat(dialogTextMismatch)
      .hasToString(expectedString);
  }
}
