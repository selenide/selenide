package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.appear;
import static org.mockito.Mockito.mock;

class ElementShouldNotTest implements WithAssertions {
  private Driver driver = new DriverStub();

  @Test
  void testToString() {
    ElementShouldNot elementShould = new ElementShouldNot(driver, "by.name: selenide", "be ", appear,
      mock(WebElement.class), new Throwable("Error message"));
    assertThat(elementShould).hasMessage(String.format("Element should not be visible {by.name: selenide}%n" +
      "Element: '<null displayed:false></null>'%n" +
      "Actual value: visible:false%n" +
      "Screenshot: null%n" +
      "Timeout: 0 ms.%n" +
      "Caused by: java.lang.Throwable: Error message"));
  }
}
