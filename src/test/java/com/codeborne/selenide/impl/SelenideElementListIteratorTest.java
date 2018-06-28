package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelenideElementListIteratorTest implements WithAssertions {
  @Test
  void testHasPrevious() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 1);
    assertThat(selenideElementIterator.hasPrevious())
      .isTrue();
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
    assertThat(previous)
      .isNotNull();
    assertThat(previous)
      .hasToString("<a>selenide</a>");
  }

  @Test
  void testNextIndex() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 1);
    assertThat(selenideElementIterator.nextIndex())
      .isEqualTo(2);
  }

  @Test
  void testPreviousIndex() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 3);
    assertThat(selenideElementIterator.previousIndex())
      .isEqualTo(2);
  }

  @Test
  void testAdd() {
    try {
      WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
      SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 0);
      selenideElementIterator.add(mock(SelenideElement.class));
    } catch (UnsupportedOperationException e) {
      assertThat(e)
        .hasMessage("Cannot add elements to web page");
    }
  }

  @Test
  void testSet() {
    try {
      WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
      SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 0);
      selenideElementIterator.set(mock(SelenideElement.class));
    } catch (UnsupportedOperationException e) {
      assertThat(e)
        .hasMessage("Cannot set elements to web page");
    }
  }
}
