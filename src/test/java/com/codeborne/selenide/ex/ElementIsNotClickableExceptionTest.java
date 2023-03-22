package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriverException;

import static org.assertj.core.api.Assertions.assertThat;

final class ElementIsNotClickableExceptionTest {
  private final Driver driver = new DriverStub();

  @Test
  void errorMessage() {
    WebDriverException cause = new WebDriverException("Sorry, is not clickable at the moment");
    ElementIsNotClickableException e = new ElementIsNotClickableException(driver, "#link", cause);

    assertThat(e).hasMessageStartingWith("Element is not clickable");
    assertThat(e).hasMessageContaining("WebDriverException: Sorry, is not clickable at the moment");
  }
}
