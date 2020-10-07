package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockWebElement;

final class SelenideElementIteratorTest implements WithAssertions {
  private final WebElement webElement = mockWebElement("a", "click me if you can");

  @Test
  void hasNext() {
    WebElementsCollection collection = mockCollection("collection with 1 element", webElement);
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(collection);

    assertThat(selenideElementIterator.hasNext()).isTrue();
  }

  @Test
  void doesNotHasNext() {
    WebElementsCollection collection = mockCollection("empty collection");
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(collection);

    assertThat(selenideElementIterator.hasNext()).isFalse();
  }

  @Test
  void next() {
    WebElementsCollection collection = mockCollection("collection with 1 element", webElement);
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(collection);
    SelenideElement nextElement = selenideElementIterator.next();

    assertThat(nextElement).isNotNull();
    assertThat(nextElement).hasToString("<a>click me if you can</a>");
    assertThat(selenideElementIterator.hasNext()).isFalse();
  }

  @Test
  void remove() {
    WebElementsCollection collection = mockCollection("collection with 1 element", webElement);
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(collection);

    assertThatThrownBy(selenideElementIterator::remove)
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessage("Cannot remove elements from web page");
  }
}
