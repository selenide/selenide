package com.codeborne.selenide.ex;

import java.util.List;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ElementNotFoundTest {

  @Test
  void testElementNotFoundWithByCriteria() {
    ElementNotFound elementNotFoundById = new ElementNotFound(By.id("Hello"), Condition.exist);
    String expectedMessage = "Element not found {By.id: Hello}\n" +
      "Expected: exist\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.";
    Assertions.assertEquals(expectedMessage, elementNotFoundById.toString());
  }

  @Test
  void testElementNotFoundWithStringCriteria() {
    ElementNotFound elementNotFoundById = new ElementNotFound("Hello", Condition.exist);
    String expectedMessage = "Element not found {Hello}\n" +
      "Expected: exist\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.";
    Assertions.assertEquals(expectedMessage, elementNotFoundById.toString());
  }

  @Test
  void testElementNotFoundWithStringCriteriaAndThrowableError() {
    ElementNotFound elementNotFoundById = new ElementNotFound("Hello", Condition.exist, new Throwable("Error message"));
    String expectedMessage = "Element not found {Hello}\n" +
      "Expected: exist\n" +
      "Screenshot: null\n" +
      "Timeout: 0 ms.\n" +
      "Caused by: java.lang.Throwable: Error message";
    Assertions.assertEquals(expectedMessage, elementNotFoundById.toString());
  }

  @Test
  void testElementNotFoundWithWebElementCollectionAndThrowableError() {
    WebElementsCollection webElementCollectionMock = mock(WebElementsCollection.class);
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
    Assertions.assertEquals(expectedMessage, elementNotFoundById.toString());
  }
}
