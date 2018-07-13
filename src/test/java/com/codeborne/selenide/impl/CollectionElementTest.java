package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CollectionElementTest {

  @Test
  public void testWrap() {
    WebElement mockedWebElement = mock(WebElement.class);
    when(mockedWebElement.getTagName()).thenReturn("a");
    when(mockedWebElement.isDisplayed()).thenReturn(true);
    when(mockedWebElement.getText()).thenReturn("selenide");

    WebElementsCollection collection = new WebElementsCollectionWrapper(singletonList(mockedWebElement));
    SelenideElement selenideElement = CollectionElement.wrap(collection, 0);
    assertEquals("<a>selenide</a>", selenideElement.toString());

  }

  @Test
  public void testGetWebElement() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    WebElement mockedWebElement1 = mock(WebElement.class);
    WebElement mockedWebElement2 = mock(WebElement.class);
    List<WebElement> listOfMockedElements = asList(mockedWebElement1, mockedWebElement2);
    when(mockedWebElementCollection.getElements()).thenReturn(listOfMockedElements);
    CollectionElement collectionElement = new CollectionElement(mockedWebElementCollection, 1);

    assertEquals(mockedWebElement2, collectionElement.getWebElement());
  }

  @Test
  public void testGetSearchCriteria() {
    String collectionDescription = "Collection description";
    int index = 1;
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    when(mockedWebElementCollection.description()).thenReturn(collectionDescription);
    CollectionElement collectionElement = new CollectionElement(mockedWebElementCollection, 1);
    assertEquals(String.format("%s[%s]", collectionDescription, index), collectionElement.getSearchCriteria());
  }

  @Test
  public void testToString() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    String collectionDescription = "Collection description";
    when(mockedWebElementCollection.description()).thenReturn(collectionDescription);
    int index = 1;
    CollectionElement collectionElement = new CollectionElement(mockedWebElementCollection, 1);
    assertEquals(String.format("%s[%s]", collectionDescription, index), collectionElement.toString());

  }

  @Test
  public void testCreateElementNotFoundErrorWithEmptyCollection() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    String collectionDescription = "Collection description";
    when(mockedWebElementCollection.description()).thenReturn(collectionDescription);
    CollectionElement collectionElement = new CollectionElement(mockedWebElementCollection, 1);

    Condition mockedCollection = mock(Condition.class);
    ElementNotFound elementNotFoundError = collectionElement.createElementNotFoundError(mockedCollection, new Error("Error message"));

    assertEquals("Element not found {Collection description}\n" +
        "Expected: visible\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.\n" +
        "Caused by: java.lang.Error: Error message", elementNotFoundError.toString());
  }

  @Test
  public void testCreateElementNotFoundErrorWithNonEmptyCollection() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    String collectionDescription = "Collection description";
    when(mockedWebElementCollection.description()).thenReturn(collectionDescription);
    when(mockedWebElementCollection.getElements()).thenReturn(singletonList(mock(WebElement.class)));
    CollectionElement collectionElement = new CollectionElement(mockedWebElementCollection, 1);

    Condition mockedCollection = mock(Condition.class);
    when(mockedCollection.toString()).thenReturn("Reason description");
    ElementNotFound elementNotFoundError = collectionElement.createElementNotFoundError(mockedCollection, new Error("Error message"));

    assertEquals("Element not found {Collection description[1]}\n" +
        "Expected: Reason description\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.\n" +
        "Caused by: java.lang.Error: Error message", elementNotFoundError.toString());
  }

}
