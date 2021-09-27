package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class UIAssertionErrorTest {
  @Test
  void errorMessage() {
    Driver driver = new DriverStub();
    UIAssertionError uiAssertionError = new UIAssertionError(driver, "Some it happened", new Throwable("Error message"));

    assertThat(uiAssertionError).hasMessage(String.format("Some it happened%n" +
      "Timeout: 0 ms.%n" +
      "Caused by: java.lang.Throwable: Error message"));
  }
}
