package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Mocks.mockElement;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

final class TextsInAnyOrderTest {
  @Test
  void elementsInSameOrder() {
    TextsInAnyOrder texts = new TextsInAnyOrder(asList("One", "Two", "Three"));
    List<WebElement> collection = asList(mockElement("One"), mockElement("Two"), mockElement("Three"));
    assertThat(texts.test(collection)).isTrue();
  }

  @Test
  void elementsInDifferentOrder() {
    TextsInAnyOrder texts = new TextsInAnyOrder(asList("Two", "One", "Three"));
    List<WebElement> collection = asList(mockElement("One"), mockElement("Two"), mockElement("Three"));
    assertThat(texts.test(collection)).isTrue();
  }

  @Test
  void tooLongListSize() {
    TextsInAnyOrder texts = new TextsInAnyOrder(asList("Two", "One"));
    List<WebElement> collection = asList(mockElement("One"), mockElement("Two"), mockElement("Three"));
    assertThat(texts.test(collection)).isFalse();
  }

  @Test
  void tooShortList() {
    TextsInAnyOrder texts = new TextsInAnyOrder(asList("Two", "One", "four", "three"));
    List<WebElement> collection = asList(mockElement("One"), mockElement("Two"), mockElement("Three"));
    assertThat(texts.test(collection)).isFalse();
  }

  @Test
  void testToString() {
    assertThat(new TextsInAnyOrder(asList("One", "Two")))
      .hasToString("TextsInAnyOrder [One, Two]");
  }
}
