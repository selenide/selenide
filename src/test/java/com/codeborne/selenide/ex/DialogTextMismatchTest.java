package com.codeborne.selenide.ex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DialogTextMismatchTest {

  @Test
  void dialogMismatchTextStringTest() {
    DialogTextMismatch dialogTextMismatch = new DialogTextMismatch("Actual text", "Expected text");
    String expectedString = "DialogTextMismatch \n" +
      "Actual: Actual text\n" +
      "Expected: Expected text\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.";
    Assertions.assertEquals(expectedString, dialogTextMismatch.toString());
  }
}
