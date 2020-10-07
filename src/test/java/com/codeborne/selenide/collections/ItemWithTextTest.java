package com.codeborne.selenide.collections;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementWithTextNotFound;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockElement;

final class ItemWithTextTest implements WithAssertions {
  private final SelenideElement element1 = mockElement("Test-One");
  private final SelenideElement element2 = mockElement("Test-Two");
  private final SelenideElement element3 = mockElement("Test-Three");
  private final WebElementsCollection collection = mockCollection("Collection description", element1, element2, element3);

  @Test
  void applyOnCorrectElementText() {
    assertThat(new ItemWithText("Test-One")
      .test(collection.getElements()))
      .isTrue();
  }

  @Test
  void applyOnWrongElementText() {
    assertThat(new ItemWithText("Test-X")
      .test(collection.getElements()))
      .isFalse();
  }

  @Test
  void testToString() {
    assertThat(new ItemWithText("Test-One"))
      .hasToString("Text Test-One");
  }

  @Test
  void testApplyWithEmptyList() {
    WebElementsCollection emptyCollection = mockCollection("empty collection");
    assertThat(new ItemWithText("Test-X")
      .test(emptyCollection.getElements()))
      .isFalse();
  }

  @Test
  void failOnMatcherError() {
    String expectedText = "Won't exist";
    assertThatThrownBy(() -> new ItemWithText(expectedText)
      .fail(collection,
        collection.getElements(),
        new Exception("Exception message"), 10000)).isInstanceOf(ElementWithTextNotFound.class)
      .hasMessageContaining(
        String.format(String.format("Element with text not found" +
          "%nActual: %s" +
          "%nExpected: %s", ElementsCollection.texts(collection.getElements()), Collections.singletonList(expectedText))));
  }
}
