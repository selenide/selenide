package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelenideElementListIteratorTest {

  @Test
  void testHasPrevious() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 1);
    Assertions.assertTrue(selenideElementIterator.hasPrevious());
  }

  @Test
  void testPrevious() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    WebElement mockedWebElement = mock(WebElement.class);
    when(mockedWebElement.isDisplayed()).thenReturn(true);
    when(mockedWebElement.getTagName()).thenReturn("a");
    when(mockedWebElement.getText()).thenReturn("selenide");

    when(mockedWebElementCollection.getActualElements()).thenReturn(singletonList(mockedWebElement));

    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 1);
    SelenideElement previous = selenideElementIterator.previous();
    Assertions.assertTrue(previous != null);
    Assertions.assertEquals("<a>selenide</a>", previous.toString());
  }

  @Test
  void testNextIndex() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 1);
    Assertions.assertEquals(2, selenideElementIterator.nextIndex());
  }

  @Test
  void testPreviousIndex() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 3);
    Assertions.assertEquals(2, selenideElementIterator.previousIndex());
  }

  @Test
  void testAdd() {
    try {
      WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
      SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 0);
      selenideElementIterator.add(mock(SelenideElement.class));
    } catch (UnsupportedOperationException e) {
      Assertions.assertEquals("Cannot add elements to web page", e.getMessage());
    }
  }

  @Test
  void testSet() {
    try {
      WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
      SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 0);
      selenideElementIterator.set(mock(SelenideElement.class));
    } catch (UnsupportedOperationException e) {
      Assertions.assertEquals("Cannot set elements to web page", e.getMessage());
    }
  }
}
