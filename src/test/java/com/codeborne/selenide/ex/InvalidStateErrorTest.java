package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.StaleElementReferenceException;

import static org.assertj.core.api.Assertions.assertThat;

final class InvalidStateErrorTest {
  private final Driver driver = new DriverStub();

  @Test
  void constructorWithCause() {
    StaleElementReferenceException cause = new StaleElementReferenceException("Houston, we have a problem");
    InvalidStateError error = new InvalidStateError(driver, "#link", cause);

    assertThat(error).hasMessageStartingWith("Invalid element state");
    assertThat(error).hasMessageEndingWith("StaleElementReferenceException: Houston, we have a problem");
    assertThat(error).hasToString(String.format(
      "Invalid element state [#link]: " +
      "Houston, we have a problem%n" +
      "Timeout: 0 ms.%n" +
      "Caused by: StaleElementReferenceException: Houston, we have a problem"));
  }

  @Test
  void constructorWithMessage() {
    InvalidStateError error = new InvalidStateError(driver, "#link", "Houston, we have a problem");

    assertThat(error).hasMessageStartingWith("Invalid element state [#link]: Houston, we have a problem");
    assertThat(error).hasToString(String.format(
      "Invalid element state [#link]: Houston, we have a problem%n" +
      "Timeout: 0 ms."));
  }
}
