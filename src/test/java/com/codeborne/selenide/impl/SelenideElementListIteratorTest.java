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

@SuppressWarnings("deprecation")
final class SelenideElementListIteratorTest {
  private final WebElement webElement = mockWebElement("a", "click me if you can");
  private final CollectionSource collection = mockCollection("Collection description", webElement);

  @Test
  void hasPrevious() {
    SelenideElementListIterator it = new SelenideElementListIterator(collection, 1);
    assertThat(it.hasPrevious()).isTrue();
  }

  @Test
  void previous() {
    WebElement mockedWebElement = mockWebElement("a", "hello");
    when(collection.getElements()).thenReturn(singletonList(mockedWebElement));

    SelenideElementListIterator it = new SelenideElementListIterator(collection, 1);
    SelenideElement previous = it.previous();
    assertThat(previous).isNotNull();
    assertThat(previous).hasToString("Collection description[0]");
  }

  @Test
  void nextIndex() {
    SelenideElementListIterator it = new SelenideElementListIterator(collection, 1);
    assertThat(it.nextIndex()).isEqualTo(1);
  }

  @Test
  void previousIndex() {
    SelenideElementListIterator it = new SelenideElementListIterator(collection, 3);
    assertThat(it.previousIndex()).isEqualTo(2);
  }

  @Test
  void add() {
    SelenideElementListIterator it = new SelenideElementListIterator(collection, 0);
    assertThatThrownBy(() ->
      it.add(mock())
    ).isInstanceOf(UnsupportedOperationException.class)
      .hasMessage("Cannot add elements to web page");
  }

  @Test
  void set() {
    SelenideElementListIterator it = new SelenideElementListIterator(collection, 0);
    assertThatThrownBy(() ->
      it.set(mock())
    ).isInstanceOf(UnsupportedOperationException.class)
      .hasMessage("Cannot set elements to web page");
  }
}
