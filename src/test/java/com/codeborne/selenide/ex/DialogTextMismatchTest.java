package com.codeborne.selenide.ex;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class DialogTextMismatchTest {

  @Test
  public void dialogMismatchTextStringTest() {
    DialogTextMismatch dialogTextMismatch = new DialogTextMismatch("Actual text", "Expected text");
    String expectedString = "DialogTextMismatch \n" +
        "Actual: Actual text\n" +
        "Expected: Expected text\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.";
    assertEquals(expectedString, dialogTextMismatch.toString());
  }
}
