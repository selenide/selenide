package com.codeborne.selenide.ex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InvalidStateExceptionTest {

  @Test
  void testThrowableConstructor() {
    InvalidStateException invalidStateException = new InvalidStateException(new Throwable("Error message"));
    String expectedString = "java.lang.Throwable: Error message\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.\n" +
      "Caused by: java.lang.Throwable: Error message";
    Assertions.assertEquals(expectedString, invalidStateException.toString());
  }

  @Test
  void testStringConstructor() {
    InvalidStateException invalidStateException = new InvalidStateException("Error message");
    String expectedString = "InvalidStateException Error message\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.";
    Assertions.assertEquals(expectedString, invalidStateException.toString());
  }
}
