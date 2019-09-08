package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriverException;

class ElementIsNotClickableExceptionTest implements WithAssertions {
  @Test
  void errorMessage() {
    Driver driver = new DriverStub();
    WebDriverException cause = new WebDriverException("Sorry, is not clickable at the moment");
    ElementIsNotClickableException e = new ElementIsNotClickableException(driver, cause);

    assertThat(e).hasMessageStartingWith("WebDriverException: Sorry, is not clickable at the moment");
  }
}
