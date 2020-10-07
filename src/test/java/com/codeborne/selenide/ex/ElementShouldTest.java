package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.lang.System.lineSeparator;
import static org.mockito.Mockito.mock;

final class ElementShouldTest implements WithAssertions {
  @Test
  void testToString() {
    String searchCriteria = "by.name: selenide";
    String prefix = "be ";
    Driver driver = new DriverStub();
    WebElement webElementMock = mock(WebElement.class);
    Exception exception = new Exception("Error message");
    ElementShould elementShould = new ElementShould(driver, searchCriteria, prefix, Condition.appear, webElementMock, exception);

    assertThat(elementShould)
      .hasMessage("Element should be visible {by.name: selenide}" + lineSeparator() +
        "Element: '<null displayed:false></null>'" + lineSeparator() +
        "Actual value: visible:false" + lineSeparator() +
        "Screenshot: null" + lineSeparator() +
        "Timeout: 0 ms." + lineSeparator() +
        "Caused by: java.lang.Exception: Error message");
  }
}
