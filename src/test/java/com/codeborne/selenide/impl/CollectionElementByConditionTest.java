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

public class CollectionElementByConditionTest {

  @Test
  public void testWrap() {
    WebElement mockedWebElement = Mockito.mock(WebElement.class);
    when(mockedWebElement.getTagName()).thenReturn("a");
    when(mockedWebElement.isDisplayed()).thenReturn(true);
    when(mockedWebElement.getText()).thenReturn("selenide");

    SelenideElement selenideElement = CollectionElementByCondition.wrap(
        new WebElementsCollectionWrapper(Collections.singletonList(
            mockedWebElement)), Condition.visible);
    assertEquals("<a>selenide</a>", selenideElement.toString());

  }


  @Test
  public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(mockedWebElementCollection, Condition.visible);

    Field collectionField = collectionElement.getClass().getDeclaredField("collection");
    collectionField.setAccessible(true);
    Field conditionField = collectionElement.getClass().getDeclaredField("condition");
    conditionField.setAccessible(true);

    assertEquals(mockedWebElementCollection, collectionField.get(collectionElement));
    assertEquals(Condition.visible, conditionField.get(collectionElement));
  }

  @Test
  public void testGetWebElement() {
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    WebElement mockedWebElement1 = Mockito.mock(WebElement.class);
    WebElement mockedWebElement2 = Mockito.mock(WebElement.class);

    List<WebElement> listOfMockedElements = Arrays.asList(mockedWebElement1, mockedWebElement2);
    when(mockedWebElementCollection.getActualElements()).thenReturn(listOfMockedElements);
    when(mockedWebElement2.isDisplayed()).thenReturn(true);
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(mockedWebElementCollection, Condition.visible);

    assertEquals(mockedWebElement2, collectionElement.getWebElement());
  }

  @Test
  public void testGetSearchCriteria() {
    String collectionDescription = "Collection description";
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    when(mockedWebElementCollection.description()).thenReturn(collectionDescription);
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(mockedWebElementCollection, Condition.visible);
    assertEquals(String.format("%s.findBy(visible)", collectionDescription), collectionElement.toString());
  }

  @Test
  public void testToString() {
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    String collectionDescription = "Collection description";
    when(mockedWebElementCollection.description()).thenReturn(collectionDescription);
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(mockedWebElementCollection, Condition.visible);
    assertEquals(String.format("%s.findBy(visible)", collectionDescription), collectionElement.toString());

  }

  @Test
  public void testCreateElementNotFoundErrorWithEmptyCollection() {
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    String collectionDescription = "Collection description";
    when(mockedWebElementCollection.description()).thenReturn(collectionDescription);
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(mockedWebElementCollection, Condition.visible);

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
    when(mockedWebElementCollection.getActualElements()).thenReturn(Collections.singletonList(Mockito.mock(WebElement.class)));
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(mockedWebElementCollection, Condition.visible);

    Condition mockedCollection = Mockito.mock(Condition.class);
    when(mockedCollection.toString()).thenReturn("Reason description");
    ElementNotFound elementNotFoundError = collectionElement.createElementNotFoundError(mockedCollection, new Error("Error message"));

    assertEquals("Element not found {Collection description.findBy(visible)}\n" +
        "Expected: Reason description\n" +
        "Screenshot: null\n" +
        "Timeout: 0 ms.\n" +
        "Caused by: java.lang.Error: Error message", elementNotFoundError.toString());
  }
}
