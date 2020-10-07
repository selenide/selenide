package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.StaleElementReferenceException;

final class InvalidStateExceptionTest implements WithAssertions {
  private Driver driver = new DriverStub();

  @Test
  void constructorWithCause() {
    StaleElementReferenceException cause = new StaleElementReferenceException("Houston, we have a problem");
    InvalidStateException invalidStateException = new InvalidStateException(driver, cause);

    assertThat(invalidStateException).hasMessageStartingWith("Invalid element state");
    assertThat(invalidStateException).hasMessageEndingWith("StaleElementReferenceException: Houston, we have a problem");
    assertThat(invalidStateException).hasToString(String.format("Invalid element state: " +
      "Houston, we have a problem%n" +
      "Screenshot: null%n" +
      "Timeout: 0 ms.%n" +
      "Caused by: StaleElementReferenceException: Houston, we have a problem"));
  }

  @Test
  void constructorWithMessage() {
    InvalidStateException invalidStateException = new InvalidStateException(driver, "Houston, we have a problem");

    assertThat(invalidStateException).hasMessageStartingWith("Invalid element state: Houston, we have a problem");
    assertThat(invalidStateException).hasToString(String.format("Invalid element state: Houston, we have a problem%n" +
      "Screenshot: null%n" +
      "Timeout: 0 ms."));
  }
}
