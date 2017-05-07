package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.By;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

public class ElementNotFoundTest {

  @Test
  public void testElementNotFoundWithByCriteria() {
    ElementNotFound elementNotFoundById = new ElementNotFound(By.id("Hello"), Condition.exist);
    String expectedMessage = "Element not found {By.id: Hello}\n" +
        "Expected: exist\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.";
    assertEquals(expectedMessage, elementNotFoundById.toString());
  }

  @Test
  public void testElementNotFoundWithStringCriteria() {
    ElementNotFound elementNotFoundById = new ElementNotFound("Hello", Condition.exist);
    String expectedMessage = "Element not found {Hello}\n" +
        "Expected: exist\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.";
    assertEquals(expectedMessage, elementNotFoundById.toString());
  }

  @Test
  public void testElementNotFoundWithStringCriteriaAndThrowableError() {
    ElementNotFound elementNotFoundById = new ElementNotFound("Hello", Condition.exist, new Throwable("Error message"));
    String expectedMessage = "Element not found {Hello}\n" +
        "Expected: exist\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.\n" +
        "Caused by: java.lang.Throwable: Error message";
    assertEquals(expectedMessage, elementNotFoundById.toString());
  }

  @Test
  public void testElementNotFoundWithWebElementCollectionAndThrowableError() {
    WebElementsCollection webElementCollectionMock = Mockito.mock(WebElementsCollection.class);
    when(webElementCollectionMock.description()).thenReturn("mock collection description");
    List<String> expectedStrings = Arrays.asList("One", "Two", "Three");

    ElementNotFound elementNotFoundById = new ElementNotFound(webElementCollectionMock,
                                                              expectedStrings,
                                                              new Throwable("Error message"));
    String expectedMessage = "Element not found {mock collection description}\n" +
        "Expected: [One, Two, Three]\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.\n" +
        "Caused by: java.lang.Throwable: Error message";
    assertEquals(expectedMessage, elementNotFoundById.toString());
  }
}
