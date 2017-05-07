package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class SelenideElementIteratorTest {

  @Test
  public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);

    Field indexField = selenideElementIterator.getClass().getDeclaredField("index");
    indexField.setAccessible(true);
    Field collectionField = selenideElementIterator.getClass().getDeclaredField("collection");
    collectionField.setAccessible(true);

    assertEquals(0, indexField.get(selenideElementIterator));
    assertEquals(mockedWebElementCollection, collectionField.get(selenideElementIterator));
  }

  @Test
  public void testHasNext() {
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    when(mockedWebElementCollection.getActualElements()).thenReturn(Collections.singletonList(Mockito.mock(WebElement.class)));
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
    assertTrue(selenideElementIterator.hasNext());
  }

  @Test
  public void testDoesNotHasNext() {
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    when(mockedWebElementCollection.getActualElements()).thenReturn(Collections.emptyList());
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
    assertFalse(selenideElementIterator.hasNext());
  }

  @Test
  public void testNext() {
    WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
    WebElement mockedWebElement = Mockito.mock(WebElement.class);
    when(mockedWebElement.isDisplayed()).thenReturn(true);
    when(mockedWebElement.getTagName()).thenReturn("a");
    when(mockedWebElement.getText()).thenReturn("selenide");

    when(mockedWebElementCollection.getActualElements()).thenReturn(Collections.singletonList(mockedWebElement));
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
    SelenideElement nextElement = selenideElementIterator.next();

    assertTrue(nextElement != null);
    assertEquals("<a>selenide</a>", nextElement.toString());
    assertFalse(selenideElementIterator.hasNext());
  }

  @Test
  public void testRemove() {
    try {
      WebElementsCollection mockedWebElementCollection = Mockito.mock(WebElementsCollection.class);
      SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
      selenideElementIterator.remove();
    } catch (UnsupportedOperationException e) {
      assertEquals("Cannot remove elements from web page", e.getMessage());
    }
  }
}
