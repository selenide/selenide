package com.codeborne.selenide.ex;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.collections.ExactTexts;
import com.codeborne.selenide.impl.Alias;
import com.codeborne.selenide.impl.CollectionSource;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.List;

import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.impl.Alias.NONE;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

final class ElementNotFoundTest {
  private final Driver driver = new DriverStub();

  @Test
  void elementNotFoundWithByCriteria() {
    ElementNotFound elementNotFoundById = new ElementNotFound(driver, NONE, By.id("Hello"), Condition.exist);
    String expectedMessage = String.format("Element not found {By.id: Hello}%n" +
      "Expected: exist%n" +
      "Timeout: 0 ms.");
    assertThat(elementNotFoundById).hasMessage(expectedMessage);
  }

  @Test
  void elementNotFoundWithAlias() {
    ElementNotFound elementNotFoundById = new ElementNotFound(driver, new Alias("The Hello title"), By.id("Hello"), Condition.exist);
    String expectedMessage = String.format("Element \"The Hello title\" not found {By.id: Hello}%n" +
      "Expected: exist%n" +
      "Timeout: 0 ms.");
    assertThat(elementNotFoundById).hasMessage(expectedMessage);
  }

  @Test
  void elementNotFoundWithStringCriteria() {
    ElementNotFound elementNotFoundById = new ElementNotFound(driver, NONE, "Hello", Condition.exist);
    String expectedMessage = String.format("Element not found {Hello}%n" +
      "Expected: exist%n" +
      "Timeout: 0 ms.");
    assertThat(elementNotFoundById).hasMessage(expectedMessage);
  }

  @Test
  void elementNotFoundWithStringCriteriaAndThrowableError() {
    ElementNotFound elementNotFoundById = new ElementNotFound(driver, NONE, "Hello", Condition.exist, new Throwable("Error message"));
    String expectedMessage = String.format("Element not found {Hello}%n" +
      "Expected: exist%n" +
      "Timeout: 0 ms.%n" +
      "Caused by: java.lang.Throwable: Error message");
    assertThat(elementNotFoundById).hasMessage(expectedMessage);
  }

  @Test
  void elementNotFoundWithWebElementCollectionAndThrowableError() {
    CollectionSource webElementCollectionMock = mockCollection("mock collection description");
    List<String> expectedStrings = asList("One", "Two", "Three");

    ElementNotFound elementNotFoundById = new ElementNotFound(webElementCollectionMock,
      new ExactTexts(expectedStrings).toString(), 8000,
      new Throwable("Error message"));
    String expectedMessage = String.format("Element not found {mock collection description}%n" +
      "Expected: Exact texts [One, Two, Three]%n" +
      "Timeout: 8 s.%n" +
      "Caused by: java.lang.Throwable: Error message");
    assertThat(elementNotFoundById).hasMessage(expectedMessage);
  }
}
