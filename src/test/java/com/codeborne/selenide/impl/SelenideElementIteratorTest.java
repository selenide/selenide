package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Mocks.mockCollection;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelenideElementIteratorTest implements WithAssertions {
  private WebElementsCollection collection = mockCollection("Collection description");

  @Test
  void hasNext() {
    when(collection.getElements()).thenReturn(singletonList(mock(WebElement.class)));
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(collection);
    assertThat(selenideElementIterator.hasNext())
      .isTrue();
  }

  @Test
  void doesNotHasNext() {
    when(collection.getElements()).thenReturn(emptyList());
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(collection);
    assertThat(selenideElementIterator.hasNext())
      .isFalse();
  }

  @Test
  void next() {
    WebElement mockedWebElement = mock(WebElement.class);
    when(mockedWebElement.isDisplayed()).thenReturn(true);
    when(mockedWebElement.getTagName()).thenReturn("a");
    when(mockedWebElement.getText()).thenReturn("selenide");

    when(collection.getElements()).thenReturn(singletonList(mockedWebElement));
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(collection);
    SelenideElement nextElement = selenideElementIterator.next();

    assertThat(nextElement).isNotNull();
    assertThat(nextElement).hasToString("<a>selenide</a>");
    assertThat(selenideElementIterator.hasNext()).isFalse();
  }

  @Test
  void remove() {
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(collection);

    assertThatThrownBy(selenideElementIterator::remove)
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessage("Cannot remove elements from web page");
  }
}
