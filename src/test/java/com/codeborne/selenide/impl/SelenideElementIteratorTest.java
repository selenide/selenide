package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelenideElementIteratorTest implements WithAssertions {
  @Test
  void testHasNext() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    when(mockedWebElementCollection.getActualElements()).thenReturn(singletonList(mock(WebElement.class)));
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
    assertThat(selenideElementIterator.hasNext())
      .isTrue();
  }

  @Test
  void testDoesNotHasNext() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    when(mockedWebElementCollection.getActualElements()).thenReturn(emptyList());
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
    assertThat(selenideElementIterator.hasNext())
      .isFalse();
  }

  @Test
  void testNext() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    WebElement mockedWebElement = mock(WebElement.class);
    when(mockedWebElement.isDisplayed()).thenReturn(true);
    when(mockedWebElement.getTagName()).thenReturn("a");
    when(mockedWebElement.getText()).thenReturn("selenide");

    when(mockedWebElementCollection.getActualElements()).thenReturn(singletonList(mockedWebElement));
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
    SelenideElement nextElement = selenideElementIterator.next();

    assertThat(nextElement)
      .isNotNull();
    assertThat(nextElement)
      .hasToString("<a>selenide</a>");
    assertThat(selenideElementIterator.hasNext())
      .isFalse();
  }

  @Test
  void testRemove() {
    try {
      WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
      SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
      selenideElementIterator.remove();
    } catch (UnsupportedOperationException e) {
      assertThat(e)
        .hasMessage("Cannot remove elements from web page");
    }
  }
}
