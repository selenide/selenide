package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.appear;
import static java.lang.System.lineSeparator;
import static org.mockito.Mockito.mock;

final class ElementShouldNotTest implements WithAssertions {
  private final Driver driver = new DriverStub();

  @Test
  void testToString() {
    ElementShouldNot elementShould = new ElementShouldNot(driver, "by.name: selenide", "be ", appear,
      mock(WebElement.class), new Throwable("Error message"));
    assertThat(elementShould).hasMessage("Element should not be visible {by.name: selenide}" + lineSeparator() +
      "Element: '<null displayed:false></null>'" + lineSeparator() +
      "Actual value: visible:false" + lineSeparator() +
      "Screenshot: null" + lineSeparator() +
      "Timeout: 0 ms." + lineSeparator() +
      "Caused by: java.lang.Throwable: Error message");
  }
}
