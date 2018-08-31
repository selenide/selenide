package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.extension.MockWebDriverExtension;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebElement;

import static org.mockito.Mockito.mock;

@ExtendWith(MockWebDriverExtension.class)
class ElementShouldNotTest implements WithAssertions {
  @Test
  void testToString() {
    String searchCriteria = "by.name: selenide";
    String prefix = "be ";
    String message = "message";
    WebElement webElementMock = mock(WebElement.class);
    Throwable exception = new Throwable("Error message");
    ElementShouldNot elementShould = new ElementShouldNot(searchCriteria, prefix, message, Condition.appear, webElementMock, exception);
    String expectedString = "Element should not be visible {by.name: selenide} because message\n" +
      "Element: '<null displayed:false></null>'\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.\n" +
      "Caused by: java.lang.Throwable: Error message";
    assertThat(elementShould)
      .hasToString(expectedString);
  }
}
