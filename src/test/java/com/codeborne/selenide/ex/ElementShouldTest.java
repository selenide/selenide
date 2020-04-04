package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.mock;

class ElementShouldTest implements WithAssertions {
  @Test
  void testToString() {
    String searchCriteria = "by.name: selenide";
    String prefix = "be ";
    Driver driver = new DriverStub();
    WebElement webElementMock = mock(WebElement.class);
    Exception exception = new Exception("Error message");
    ElementShould elementShould = new ElementShould(driver, searchCriteria, prefix, Condition.appear, webElementMock, exception);

    assertThat(elementShould)
      .hasMessage(String.format("Element should be visible {by.name: selenide}%n" +
        "Element: '<null displayed:false></null>'%n" +
        "Actual value: visible:false%n" +
        "Screenshot: null%n" +
        "Timeout: 0 ms.%n" +
        "Caused by: java.lang.Exception: Error message"));
  }
}
