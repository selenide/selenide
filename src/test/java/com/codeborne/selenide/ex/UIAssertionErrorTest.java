package com.codeborne.selenide.ex;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UIAssertionErrorTest {

  @Test
  public void testThrowableConstructor() {
    UIAssertionError uiAssertionError = new UIAssertionError(new Throwable("Error message"));
    String expectedString = "UIAssertionError Throwable: Error message\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.\n" +
        "Caused by: java.lang.Throwable: Error message";
    assertEquals(expectedString, uiAssertionError.toString());
  }
}
