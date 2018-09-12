package com.codeborne.selenide.ex;

import com.codeborne.selenide.Driver;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.appear;
import static org.mockito.Mockito.mock;

class ElementShouldNotTest implements WithAssertions {
  private Driver driver = mock(Driver.class);

  @Test
  void testToString() {
    ElementShouldNot elementShould = new ElementShouldNot("by.name: selenide", "be ", "message", appear, driver,
      mock(WebElement.class), new Throwable("Error message"));
    assertThat(elementShould).hasToString("Element should not be visible {by.name: selenide} because message\n" +
      "Element: '<null displayed:false></null>'\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.\n" +
      "Caused by: java.lang.Throwable: Error message");
  }
}
