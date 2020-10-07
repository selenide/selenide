package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.collections.ExactTexts;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class ElementNotFoundTest implements WithAssertions {
  private Driver driver = new DriverStub();

  @Test
  void elementNotFoundWithByCriteria() {
    ElementNotFound elementNotFoundById = new ElementNotFound(driver, By.id("Hello"), Condition.exist);
    String expectedMessage = String.format("Element not found {By.id: Hello}%n" +
      "Expected: exist%n" +
      "Screenshot: null%n" +
      "Timeout: 0 ms.");
    assertThat(elementNotFoundById).hasMessage(expectedMessage);
  }

  @Test
  void elementNotFoundWithStringCriteria() {
    ElementNotFound elementNotFoundById = new ElementNotFound(driver, "Hello", Condition.exist);
    String expectedMessage = String.format("Element not found {Hello}%n" +
      "Expected: exist%n" +
      "Screenshot: null%n" +
      "Timeout: 0 ms.");
    assertThat(elementNotFoundById).hasMessage(expectedMessage);
  }

  @Test
  void elementNotFoundWithStringCriteriaAndThrowableError() {
    ElementNotFound elementNotFoundById = new ElementNotFound(driver, "Hello", Condition.exist, new Throwable("Error message"));
    String expectedMessage = String.format("Element not found {Hello}%n" +
      "Expected: exist%n" +
      "Screenshot: null%n" +
      "Timeout: 0 ms.%n" +
      "Caused by: java.lang.Throwable: Error message");
    assertThat(elementNotFoundById).hasMessage(expectedMessage);
  }

  @Test
  void elementNotFoundWithWebElementCollectionAndThrowableError() {
    WebElementsCollection webElementCollectionMock = mock(WebElementsCollection.class);
    when(webElementCollectionMock.driver()).thenReturn(driver);
    when(webElementCollectionMock.description()).thenReturn("mock collection description");
    List<String> expectedStrings = asList("One", "Two", "Three");

    ElementNotFound elementNotFoundById = new ElementNotFound(webElementCollectionMock,
      new ExactTexts(expectedStrings).toString(),
      new Throwable("Error message"));
    String expectedMessage = String.format("Element not found {mock collection description}%n" +
      "Expected: Exact texts [One, Two, Three]%n" +
      "Screenshot: null%n" +
      "Timeout: 0 ms.%n" +
      "Caused by: java.lang.Throwable: Error message");
    assertThat(elementNotFoundById).hasMessage(expectedMessage);
  }
}
