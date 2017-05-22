package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class CollectionElementTest {

  @Test
  public void testWrap() {
    WebElement mockedWebElement = Mockito.mock(WebElement.class);
    when(mockedWebElement.getTagName()).thenReturn("a");
    when(mockedWebElement.isDisplayed()).thenReturn(true);
    when(mockedWebElement.getText()).thenReturn("selenide");

    SelenideElement selenideElement = CollectionElement.wrap(
        new WebElementsCollectionWrapper(Collections.singletonList(
            mockedWebElement)), 0);
    assertEquals("<a>selenide</a>", selenideElement.toString());

  }


  @Test
  public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    CollectionElement collectionElement = new CollectionElement(mockedWebElementCollection, 1);

    Field collectionField = collectionElement.getClass().getDeclaredField("collection");
    collectionField.setAccessible(true);
    Field indexField = collectionElement.getClass().getDeclaredField("index");
    indexField.setAccessible(true);

    assertEquals(mockedWebElementCollection, collectionField.get(collectionElement));
    assertEquals(1, indexField.get(collectionElement));
  }

  @Test
  public void testGetWebElement() {
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    WebElement mockedWebElement1 = Mockito.mock(WebElement.class);
    WebElement mockedWebElement2 = Mockito.mock(WebElement.class);
    List<WebElement> listOfMockedElements = Arrays.asList(mockedWebElement1, mockedWebElement2);
    when(mockedWebElementCollection.getActualElements()).thenReturn(listOfMockedElements);
    CollectionElement collectionElement = new CollectionElement(mockedWebElementCollection, 1);

    assertEquals(mockedWebElement2, collectionElement.getWebElement());
  }

  @Test
  public void testGetSearchCriteria() {
    String collectionDescription = "Collection description";
    int index = 1;
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    when(mockedWebElementCollection.description()).thenReturn(collectionDescription);
    CollectionElement collectionElement = new CollectionElement(mockedWebElementCollection, index);
    assertEquals(String.format("%s[%s]", collectionDescription, index), collectionElement.getSearchCriteria());
  }

  @Test
  public void testToString() {
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    String collectionDescription = "Collection description";
    when(mockedWebElementCollection.description()).thenReturn(collectionDescription);
    int index = 1;
    CollectionElement collectionElement = new CollectionElement(mockedWebElementCollection, index);
    assertEquals(String.format("%s[%s]", collectionDescription, index), collectionElement.toString());

  }

  @Test
  public void testCreateElementNotFoundErrorWithEmptyCollection() {
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    String collectionDescription = "Collection description";
    when(mockedWebElementCollection.description()).thenReturn(collectionDescription);
    int index = 1;
    CollectionElement collectionElement = new CollectionElement(mockedWebElementCollection, index);

    Condition mockedCollection = Mockito.mock(Condition.class);
    ElementNotFound elementNotFoundError = collectionElement.createElementNotFoundError(mockedCollection, new Error("Error message"));

    assertEquals("Element not found {Collection description}\n" +
        "Expected: visible\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.\n" +
        "Caused by: java.lang.Error: Error message", elementNotFoundError.toString());
  }

  @Test
  public void testCreateElementNotFoundErrorWithNonEmptyCollection() {
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    String collectionDescription = "Collection description";
    when(mockedWebElementCollection.description()).thenReturn(collectionDescription);
    when(mockedWebElementCollection.getActualElements()).thenReturn(Arrays.asList(Mockito.mock(WebElement.class)));
    int index = 1;
    CollectionElement collectionElement = new CollectionElement(mockedWebElementCollection, index);

    Condition mockedCollection = Mockito.mock(Condition.class);
    when(mockedCollection.toString()).thenReturn("Reason description");
    ElementNotFound elementNotFoundError = collectionElement.createElementNotFoundError(mockedCollection, new Error("Error message"));

    assertEquals("Element not found {Collection description[1]}\n" +
        "Expected: Reason description\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.\n" +
        "Caused by: java.lang.Error: Error message", elementNotFoundError.toString());
  }

}
