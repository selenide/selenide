package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ElementNotFoundTest implements WithAssertions {
  private Driver driver = new DriverStub();

  @Test
  void testElementNotFoundWithByCriteria() {
    ElementNotFound elementNotFoundById = new ElementNotFound(driver, By.id("Hello"), Condition.exist);
    String expectedMessage = "Element not found {By.id: Hello}\n" +
      "Expected: exist\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.";
    assertThat(elementNotFoundById)
      .hasToString(expectedMessage);
  }

  @Test
  void testElementNotFoundWithStringCriteria() {
    ElementNotFound elementNotFoundById = new ElementNotFound(driver, "Hello", Condition.exist);
    String expectedMessage = "Element not found {Hello}\n" +
      "Expected: exist\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.";
    assertThat(elementNotFoundById)
      .hasToString(expectedMessage);
  }

  @Test
  void testElementNotFoundWithStringCriteriaAndThrowableError() {
    ElementNotFound elementNotFoundById = new ElementNotFound(driver, "Hello", Condition.exist, new Throwable("Error message"));
    String expectedMessage = "Element not found {Hello}\n" +
      "Expected: exist\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.\n" +
      "Caused by: java.lang.Throwable: Error message";
    assertThat(elementNotFoundById)
      .hasToString(expectedMessage);
  }

  @Test
  void testElementNotFoundWithWebElementCollectionAndThrowableError() {
    WebElementsCollection webElementCollectionMock = mock(WebElementsCollection.class);
    when(webElementCollectionMock.driver()).thenReturn(driver);
    when(webElementCollectionMock.description()).thenReturn("mock collection description");
    List<String> expectedStrings = asList("One", "Two", "Three");

    ElementNotFound elementNotFoundById = new ElementNotFound(webElementCollectionMock,
      expectedStrings,
      new Throwable("Error message"));
    String expectedMessage = "Element not found {mock collection description}\n" +
      "Expected: [One, Two, Three]\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.\n" +
      "Caused by: java.lang.Throwable: Error message";
    assertThat(elementNotFoundById)
      .hasToString(expectedMessage);
  }
}
