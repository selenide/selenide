package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CollectionElementByConditionTest implements WithAssertions {
  private Driver driver = new DriverStub();

  @Test
  void wrap() {
    WebElement mockedWebElement = mock(WebElement.class);
    when(mockedWebElement.getTagName()).thenReturn("a");
    when(mockedWebElement.isDisplayed()).thenReturn(true);
    when(mockedWebElement.getText()).thenReturn("selenide");

    SelenideElement selenideElement = CollectionElementByCondition.wrap(
      new WebElementsCollectionWrapper(driver, singletonList(mockedWebElement)), Condition.visible);
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
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(collection, Condition.visible);

    assertThat(collectionElement.getWebElement())
      .isEqualTo(mockedWebElement2);
  }

  @Test
  void getSearchCriteria() {
    String collectionDescription = "Collection description";
    WebElementsCollection collection = mock(WebElementsCollection.class);
    when(collection.description()).thenReturn(collectionDescription);
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(collection, Condition.visible);
    assertThat(collectionElement)
      .hasToString(String.format("%s.findBy(visible)", collectionDescription));
  }

  @Test
  void testToString() {
    WebElementsCollection collection = mock(WebElementsCollection.class);
    String collectionDescription = "Collection description";
    when(collection.description()).thenReturn(collectionDescription);
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(collection, Condition.visible);
    assertThat(collectionElement)
      .hasToString(String.format("%s.findBy(visible)", collectionDescription));
  }

  @Test
  void createElementNotFoundErrorWithEmptyCollection() {
    WebElementsCollection collection = mock(WebElementsCollection.class);
    when(collection.driver()).thenReturn(driver);
    when(collection.description()).thenReturn("Collection description");
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(collection, Condition.visible);

    Condition mockedCollection = mock(Condition.class);
    ElementNotFound elementNotFoundError = collectionElement.createElementNotFoundError(mockedCollection, new Error("Error message"));

    assertThat(elementNotFoundError)
      .hasMessage(String.format("Element not found {Collection description}%n" +
        "Expected: visible%n" +
        "Screenshot: null%n" +
        "Timeout: 0 ms.%n" +
        "Caused by: java.lang.Error: Error message"));
  }

  @Test
  void createElementNotFoundErrorWithNonEmptyCollection() {
    WebElementsCollection collection = mock(WebElementsCollection.class);
    when(collection.driver()).thenReturn(driver);
    when(collection.description()).thenReturn("Collection description");
    when(collection.getElements()).thenReturn(singletonList(mock(WebElement.class)));
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(collection, Condition.visible);

    Condition mockedCollection = mock(Condition.class);
    when(mockedCollection.toString()).thenReturn("Reason description");
    ElementNotFound elementNotFoundError = collectionElement.createElementNotFoundError(mockedCollection, new Error("Error message"));

    assertThat(elementNotFoundError)
      .hasMessage(String.format("Element not found {Collection description.findBy(visible)}%n" +
        "Expected: Reason description%n" +
        "Screenshot: null%n" +
        "Timeout: 0 ms.%n" +
        "Caused by: java.lang.Error: Error message"));
  }
}
