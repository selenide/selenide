package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class CollectionElementByConditionTest implements WithAssertions {
  private Driver driver = new DriverStub();

  @Test
  void wrap() {
    WebElement mockedWebElement = mock(WebElement.class);
    when(mockedWebElement.getTagName()).thenReturn("a");
    when(mockedWebElement.isDisplayed()).thenReturn(true);
    when(mockedWebElement.getText()).thenReturn("selenide");

    SelenideElement selenideElement = CollectionElementByCondition.wrap(
      new WebElementsCollectionWrapper(driver, singletonList(mockedWebElement)), visible);
    assertThat(selenideElement)
      .hasToString("<a>selenide</a>");
  }

  @Test
  void getWebElement() {
    WebElementsCollection collection = mock(WebElementsCollection.class);
    WebElement mockedWebElement1 = mock(WebElement.class);
    WebElement mockedWebElement2 = mock(WebElement.class);

    List<WebElement> listOfMockedElements = asList(mockedWebElement1, mockedWebElement2);
    when(collection.getElements()).thenReturn(listOfMockedElements);
    when(mockedWebElement2.isDisplayed()).thenReturn(true);
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(collection, visible);

    assertThat(collectionElement.getWebElement())
      .isEqualTo(mockedWebElement2);
  }

  @Test
  void getSearchCriteria() {
    WebElementsCollection collection = mock(WebElementsCollection.class);
    when(collection.description()).thenReturn("ul#employees li.employee");
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(collection, visible);
    assertThat(collectionElement)
      .hasToString(String.format("%s.findBy(visible)", "ul#employees li.employee"));
  }

  @Test
  void testToString() {
    WebElementsCollection collection = mock(WebElementsCollection.class);
    when(collection.description()).thenReturn("ul#employees li.employee");
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(collection, visible);
    assertThat(collectionElement)
      .hasToString(String.format("%s.findBy(visible)", "ul#employees li.employee"));
  }

  @Test
  void createElementNotFoundErrorWithEmptyCollection() {
    WebElementsCollection collection = mock(WebElementsCollection.class);
    when(collection.driver()).thenReturn(driver);
    when(collection.description()).thenReturn("ul#employees li.employee");
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(collection, visible);

    ElementNotFound elementNotFoundError = collectionElement.createElementNotFoundError(visible,
      new NoSuchElementException("with class: employee"));

    assertThat(elementNotFoundError)
      .hasMessage(String.format("Element not found {ul#employees li.employee.findBy(visible)}%n" +
        "Expected: visible%n" +
        "Screenshot: null%n" +
        "Timeout: 0 ms.%n" +
        "Caused by: NoSuchElementException: with class: employee"));
  }

  @Test
  void createElementNotFoundErrorWithNonEmptyCollection() {
    WebElementsCollection collection = mock(WebElementsCollection.class);
    when(collection.driver()).thenReturn(driver);
    when(collection.description()).thenReturn("ul#employees li.employee");
    when(collection.getElements()).thenReturn(singletonList(mock(WebElement.class)));
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(collection, visible);

    Condition mockedCollection = mock(Condition.class);
    when(mockedCollection.toString()).thenReturn("Reason description");
    ElementNotFound elementNotFoundError = collectionElement.createElementNotFoundError(mockedCollection, new Error("Error message"));

    assertThat(elementNotFoundError)
      .hasMessage(String.format("Element not found {ul#employees li.employee.findBy(visible)}%n" +
        "Expected: Reason description%n" +
        "Screenshot: null%n" +
        "Timeout: 0 ms.%n" +
        "Caused by: java.lang.Error: Error message"));
  }
}
