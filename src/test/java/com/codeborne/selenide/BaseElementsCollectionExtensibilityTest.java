package com.codeborne.selenide;

import com.codeborne.selenide.impl.CollectionSource;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockWebElement;
import static org.assertj.core.api.Assertions.assertThat;

final class BaseElementsCollectionExtensibilityTest {
  interface CustomElement extends SelenideElement {
  }

  static class CustomCollection extends ElementsCollection {
    CustomCollection(CollectionSource source) {
      super(source, CustomElement.class);
    }
  }

  private final WebElement element1 = mockWebElement("h1", "Hello");
  private final WebElement element2 = mockWebElement("h2", "Mark");

  @Test
  void getReturnsAnElementImplementingTheClassPassedToTheConstructor() {
    CollectionSource source = mockCollection("custom", element1, element2);
    CustomCollection collection = new CustomCollection(source);

    assertThat(collection.get(0)).isInstanceOf(CustomElement.class);
  }

  @Test
  void firstAndLastAlsoImplementTheCustomClass() {
    CollectionSource source = mockCollection("custom", element1, element2);
    CustomCollection collection = new CustomCollection(source);

    assertThat(collection.first()).isInstanceOf(CustomElement.class);
    assertThat(collection.last()).isInstanceOf(CustomElement.class);
  }

  @Test
  void elementsProducedByIteration_alsoImplementTheCustomClass() {
    CollectionSource source = mockCollection("custom", element1, element2);
    CustomCollection collection = new CustomCollection(source);

    SelenideElement first = collection.iterator().next();

    assertThat(first).isInstanceOf(CustomElement.class);
  }

  @Test
  void elementsProducedByStream_alsoImplementTheCustomClass() {
    CollectionSource source = mockCollection("custom", element1, element2);
    CustomCollection collection = new CustomCollection(source);

    assertThat(collection.stream()).allSatisfy(element -> assertThat(element).isInstanceOf(CustomElement.class));
  }
}
