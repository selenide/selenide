package com.codeborne.selenide.ex;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.StaleElementReferenceException;

import static org.assertj.core.api.Assertions.assertThat;

final class InvalidStateErrorTest {
  @Test
  void constructorWithCause() {
    StaleElementReferenceException cause = new StaleElementReferenceException("Houston, we have a problem");
    InvalidStateError error = new InvalidStateError("#link", cause);

    assertThat(error).hasMessageStartingWith("Invalid element state");
    assertThat(error).hasMessageEndingWith("StaleElementReferenceException: Houston, we have a problem");
    assertThat(error).hasToString(String.format(
      "Invalid element state [#link]: " +
      "Houston, we have a problem%n" +
      "Caused by: StaleElementReferenceException: Houston, we have a problem"));
  }

  @Test
  void constructorWithMessage() {
    InvalidStateError error = new InvalidStateError("#link", "Houston, we have a problem");

    assertThat(error).hasMessageStartingWith("Invalid element state [#link]: Houston, we have a problem");
    assertThat(error).hasToString("Invalid element state [#link]: Houston, we have a problem");
  }
}
