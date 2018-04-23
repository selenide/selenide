package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SelenideElementIteratorTest {
  @Test
  public void testHasNext() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    when(mockedWebElementCollection.getActualElements()).thenReturn(singletonList(mock(WebElement.class)));
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
    assertTrue(selenideElementIterator.hasNext());
  }

  @Test
  public void testDoesNotHasNext() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    when(mockedWebElementCollection.getActualElements()).thenReturn(emptyList());
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
    assertFalse(selenideElementIterator.hasNext());
  }

  @Test
  public void testNext() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    WebElement mockedWebElement = mock(WebElement.class);
    when(mockedWebElement.isDisplayed()).thenReturn(true);
    when(mockedWebElement.getTagName()).thenReturn("a");
    when(mockedWebElement.getText()).thenReturn("selenide");

    when(mockedWebElementCollection.getActualElements()).thenReturn(singletonList(mockedWebElement));
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
    SelenideElement nextElement = selenideElementIterator.next();

    assertTrue(nextElement != null);
    assertEquals("<a>selenide</a>", nextElement.toString());
    assertFalse(selenideElementIterator.hasNext());
  }

  @Test
  public void testRemove() {
    try {
      WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
      SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
      selenideElementIterator.remove();
    } catch (UnsupportedOperationException e) {
      assertEquals("Cannot remove elements from web page", e.getMessage());
    }
  }
}
