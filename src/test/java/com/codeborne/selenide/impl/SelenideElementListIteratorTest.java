package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SelenideElementListIteratorTest {

  @Test
  public void testHasPrevious() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 1);
    assertTrue(selenideElementIterator.hasPrevious());
  }

  @Test
  public void testPrevious() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    WebElement mockedWebElement = mock(WebElement.class);
    when(mockedWebElement.isDisplayed()).thenReturn(true);
    when(mockedWebElement.getTagName()).thenReturn("a");
    when(mockedWebElement.getText()).thenReturn("selenide");

    when(mockedWebElementCollection.getElements()).thenReturn(singletonList(mockedWebElement));

    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 1);
    SelenideElement previous = selenideElementIterator.previous();
    assertTrue(previous != null);
    assertEquals("<a>selenide</a>", previous.toString());
  }

  @Test
  public void testNextIndex() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 1);
    assertEquals(2, selenideElementIterator.nextIndex());
  }

  @Test
  public void testPreviousIndex() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 3);
    assertEquals(2, selenideElementIterator.previousIndex());
  }


  @Test
  public void testAdd() {
    try {
      WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
      SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 0);
      selenideElementIterator.add(mock(SelenideElement.class));
    } catch (UnsupportedOperationException e) {
      assertEquals("Cannot add elements to web page", e.getMessage());
    }
  }

  @Test
  public void testSet() {
    try {
      WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
      SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 0);
      selenideElementIterator.set(mock(SelenideElement.class));
    } catch (UnsupportedOperationException e) {
      assertEquals("Cannot set elements to web page", e.getMessage());
    }
  }

}
