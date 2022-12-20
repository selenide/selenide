package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockWebElement;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class SelenideElementListIteratorTest {
  private final WebElement webElement = mockWebElement("a", "click me if you can");
  private final CollectionSource collection = mockCollection("Collection description", webElement);

  @Test
  void hasPrevious() {
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(collection, 1);
    assertThat(selenideElementIterator.hasPrevious()).isTrue();
  }

  @Test
  void previous() {
    WebElement mockedWebElement = mock();
    when(mockedWebElement.isDisplayed()).thenReturn(true);
    when(mockedWebElement.getTagName()).thenReturn("a");
    when(mockedWebElement.getText()).thenReturn("selenide");

    when(collection.getElements()).thenReturn(singletonList(mockedWebElement));

    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(collection, 1);
    SelenideElement previous = selenideElementIterator.previous();
    assertThat(previous).isNotNull();
    assertThat(previous).hasToString("<a>click me if you can</a>");
  }

  @Test
  void nextIndex() {
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(collection, 1);
    assertThat(selenideElementIterator.nextIndex()).isEqualTo(2);
  }

  @Test
  void previousIndex() {
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(collection, 3);
    assertThat(selenideElementIterator.previousIndex()).isEqualTo(2);
  }

  @Test
  void add() {
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(collection, 0);
    assertThatThrownBy(() ->
      selenideElementIterator.add(mock())
    ).isInstanceOf(UnsupportedOperationException.class)
      .hasMessage("Cannot add elements to web page");
  }

  @Test
  void set() {
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(collection, 0);
    assertThatThrownBy(() ->
      selenideElementIterator.set(mock())
    ).isInstanceOf(UnsupportedOperationException.class)
      .hasMessage("Cannot set elements to web page");
  }
}
