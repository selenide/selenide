package com.codeborne.selenide.ex;

import com.codeborne.selenide.UnitTest;
import org.junit.jupiter.api.Test;

class UIAssertionErrorTest extends UnitTest {
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
