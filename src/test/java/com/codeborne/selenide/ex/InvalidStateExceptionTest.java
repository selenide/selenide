package com.codeborne.selenide.ex;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class InvalidStateExceptionTest {

  @Test
  public void testThrowableConstructor() {
    InvalidStateException invalidStateException = new InvalidStateException(new Throwable("Error message"));
    String expectedString = "java.lang.Throwable: Error message\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.\n" +
        "Caused by: java.lang.Throwable: Error message";
    assertEquals(expectedString, invalidStateException.toString());
  }

  @Test
  public void testStringConstructor() {
    InvalidStateException invalidStateException = new InvalidStateException("Error message");
    String expectedString = "InvalidStateException Error message\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.";
    assertEquals(expectedString, invalidStateException.toString());
  }
}
