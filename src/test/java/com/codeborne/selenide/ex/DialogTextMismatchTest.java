package com.codeborne.selenide.ex;

import com.codeborne.selenide.UnitTest;
import org.junit.jupiter.api.Test;

class DialogTextMismatchTest extends UnitTest {
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
