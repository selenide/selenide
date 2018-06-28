package com.codeborne.selenide.ex;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class InvalidStateExceptionTest implements WithAssertions {
  @Test
  void testThrowableConstructor() {
    InvalidStateException invalidStateException = new InvalidStateException(new Throwable("Error message"));
    String expectedString = "java.lang.Throwable: Error message\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.\n" +
      "Caused by: java.lang.Throwable: Error message";
    assertThat(invalidStateException)
      .hasToString(expectedString);
  }

  @Test
  void testStringConstructor() {
    InvalidStateException invalidStateException = new InvalidStateException("Error message");
    String expectedString = "InvalidStateException Error message\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.";
    assertThat(invalidStateException)
      .hasToString(expectedString);
  }
}
