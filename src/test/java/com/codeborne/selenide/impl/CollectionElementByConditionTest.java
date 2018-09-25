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
  void testWrap() {
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
  void testGetWebElement() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    WebElement mockedWebElement1 = mock(WebElement.class);
    WebElement mockedWebElement2 = mock(WebElement.class);

    List<WebElement> listOfMockedElements = asList(mockedWebElement1, mockedWebElement2);
    when(mockedWebElementCollection.getElements()).thenReturn(listOfMockedElements);
    when(mockedWebElement2.isDisplayed()).thenReturn(true);
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(mockedWebElementCollection, Condition.visible);

    assertThat(collectionElement.getWebElement())
      .isEqualTo(mockedWebElement2);
  }

  @Test
  void testGetSearchCriteria() {
    String collectionDescription = "Collection description";
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    when(mockedWebElementCollection.description()).thenReturn(collectionDescription);
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(mockedWebElementCollection, Condition.visible);
    assertThat(collectionElement)
      .hasToString(String.format("%s.findBy(visible)", collectionDescription));
  }

  @Test
  void testToString() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    String collectionDescription = "Collection description";
    when(mockedWebElementCollection.description()).thenReturn(collectionDescription);
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(mockedWebElementCollection, Condition.visible);
    assertThat(collectionElement)
      .hasToString(String.format("%s.findBy(visible)", collectionDescription));
  }

  @Test
  void testCreateElementNotFoundErrorWithEmptyCollection() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    when(mockedWebElementCollection.driver()).thenReturn(driver);
    when(mockedWebElementCollection.description()).thenReturn("Collection description");
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(mockedWebElementCollection, Condition.visible);

    Condition mockedCollection = mock(Condition.class);
    ElementNotFound elementNotFoundError = collectionElement.createElementNotFoundError(mockedCollection, new Error("Error message"));

    assertThat(elementNotFoundError)
      .hasToString("Element not found {Collection description}\n" +
        "Expected: visible\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.\n" +
        "Caused by: java.lang.Error: Error message");
  }

  @Test
  void testCreateElementNotFoundErrorWithNonEmptyCollection() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    when(mockedWebElementCollection.driver()).thenReturn(driver);
    when(mockedWebElementCollection.description()).thenReturn("Collection description");
    when(mockedWebElementCollection.getElements()).thenReturn(singletonList(mock(WebElement.class)));
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(mockedWebElementCollection, Condition.visible);

    Condition mockedCollection = mock(Condition.class);
    when(mockedCollection.toString()).thenReturn("Reason description");
    ElementNotFound elementNotFoundError = collectionElement.createElementNotFoundError(mockedCollection, new Error("Error message"));

    assertThat(elementNotFoundError)
      .hasToString("Element not found {Collection description.findBy(visible)}\n" +
        "Expected: Reason description\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.\n" +
        "Caused by: java.lang.Error: Error message");
  }
}
