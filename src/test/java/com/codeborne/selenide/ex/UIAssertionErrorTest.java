package com.codeborne.selenide.ex;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class UIAssertionErrorTest implements WithAssertions {
  @Test
  void testThrowableConstructor() {
    UIAssertionError uiAssertionError = new UIAssertionError(new Throwable("Error message"));
    String expectedString = "UIAssertionError Throwable: Error message\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.\n" +
      "Caused by: java.lang.Throwable: Error message";
    assertThat(uiAssertionError)
      .hasToString(expectedString);
  }
}
