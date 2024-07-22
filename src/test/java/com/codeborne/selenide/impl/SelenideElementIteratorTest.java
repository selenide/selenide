package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockWebElement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class SelenideElementIteratorTest {
  private final WebElement webElement = mockWebElement("a", "click me if you can");

  @Test
  void hasNext() {
    CollectionSource collection = mockCollection("collection with 1 element", webElement);
    SelenideElementIterator<SelenideElement> selenideElementIterator = new SelenideElementIterator<>(collection, SelenideElement.class);

    assertThat(selenideElementIterator.hasNext()).isTrue();
  }

  @Test
  void doesNotHasNext() {
    CollectionSource collection = mockCollection("empty collection");
    SelenideElementIterator<SelenideElement> selenideElementIterator = new SelenideElementIterator<>(collection, SelenideElement.class);

    assertThat(selenideElementIterator.hasNext()).isFalse();
  }

  @Test
  void next() {
    CollectionSource collection = mockCollection("collection with 1 element", webElement);
    SelenideElementIterator<SelenideElement> selenideElementIterator = new SelenideElementIterator<>(collection, SelenideElement.class);
    SelenideElement nextElement = selenideElementIterator.next();

    assertThat(nextElement).isNotNull();
    assertThat(nextElement).hasToString("collection with 1 element[0]");
    assertThat(selenideElementIterator.hasNext()).isFalse();
  }

  @Test
  void remove() {
    CollectionSource collection = mockCollection("collection with 1 element", webElement);
    SelenideElementIterator<SelenideElement> selenideElementIterator = new SelenideElementIterator<>(collection, SelenideElement.class);

    assertThatThrownBy(selenideElementIterator::remove)
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessage("Cannot remove elements from web page");
  }
}
