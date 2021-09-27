package com.codeborne.selenide.collections;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementWithTextNotFound;
import com.codeborne.selenide.impl.CollectionSource;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static com.codeborne.selenide.ElementsCollection.texts;
import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockElement;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ItemWithTextTest {
  private final SelenideElement element1 = mockElement("Test-One");
  private final SelenideElement element2 = mockElement("Test-Two");
  private final SelenideElement element3 = mockElement("Test-Three");
  private final CollectionSource collection = mockCollection("Collection description", element1, element2, element3);

  @Test
  void correctElementText() {
    assertThat(new ItemWithText("Test-One")
      .test(collection.getElements()))
      .isTrue();
  }

  @Test
  void wrongElementText() {
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
  void emptyList() {
    assertThat(new ItemWithText("Test-X")
      .test(new ArrayList<>()))
      .isFalse();
  }

  @Test
  void failOnMatcherError() {
    String expectedText = "Won't exist";
    assertThatThrownBy(() -> new ItemWithText(expectedText)
      .fail(collection,
        collection.getElements(),
        new Exception("Exception message"), 10000)).isInstanceOf(ElementWithTextNotFound.class)
      .hasMessageContaining(String.format("Element with text not found" +
        "%nActual: %s" +
        "%nExpected: %s", texts(collection.getElements()), singletonList(expectedText)));
  }
}
