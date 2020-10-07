package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

final class UIAssertionErrorTest implements WithAssertions {
  @Test
  void errorMessage() {
    Driver driver = new DriverStub();
    UIAssertionError uiAssertionError = new UIAssertionError(driver, "Some it happened", new Throwable("Error message"));

    assertThat(uiAssertionError).hasMessage(String.format("Some it happened%n" +
      "Screenshot: null%n" +
      "Timeout: 0 ms.%n" +
      "Caused by: java.lang.Throwable: Error message"));
  }
}
