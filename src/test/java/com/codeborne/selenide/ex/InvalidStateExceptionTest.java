package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.StaleElementReferenceException;

class InvalidStateExceptionTest implements WithAssertions {
  private Driver driver = new DriverStub();

  @Test
  void constructorWithCause() {
    StaleElementReferenceException cause = new StaleElementReferenceException("Houston, we have a problem");
    InvalidStateException invalidStateException = new InvalidStateException(driver, cause);

    assertThat(invalidStateException).hasMessageStartingWith("StaleElementReferenceException: Houston, we have a problem");
    assertThat(invalidStateException).hasToString("com.codeborne.selenide.ex.InvalidStateException: " +
      "StaleElementReferenceException: Houston, we have a problem\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.\n" +
      "Caused by: StaleElementReferenceException: Houston, we have a problem");
  }

  @Test
  void constructorWithMessage() {
    InvalidStateException invalidStateException = new InvalidStateException(driver, "Houston, we have a problem");

    assertThat(invalidStateException).hasMessageStartingWith("Houston, we have a problem");
    assertThat(invalidStateException).hasToString("com.codeborne.selenide.ex.InvalidStateException: Houston, we have a problem\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.");
  }
}
