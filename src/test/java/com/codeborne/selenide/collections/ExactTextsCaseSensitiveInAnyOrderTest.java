package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Mocks.mockElement;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

final class ExactTextsCaseSensitiveInAnyOrderTest {

  @Test
  void shouldMatchWithSameOrder() {
    ExactTextsCaseSensitiveInAnyOrder texts = new ExactTextsCaseSensitiveInAnyOrder(asList("One", "Two", "Three"));
    assertThat(texts.test(asList(mockElement("One"), mockElement("Two"), mockElement("Three"))))
      .isTrue();
  }

  @Test
  void shouldMatchWithDifferentOrder() {
    ExactTextsCaseSensitiveInAnyOrder texts = new ExactTextsCaseSensitiveInAnyOrder(asList("One", "Three", "Two"));
    assertThat(texts.test(asList(mockElement("One"), mockElement("Two"), mockElement("Three"))))
      .isTrue();
  }

  @Test
  void shouldNotMatchWithSameOrderAndDifferentCase() {
    ExactTextsCaseSensitiveInAnyOrder texts = new ExactTextsCaseSensitiveInAnyOrder("one", "two", "three");
    assertThat(texts.test(asList(mockElement("One"), mockElement("Two"), mockElement("Three"))))
      .isFalse();
  }

  @Test
  void shouldNotMatchWithDifferentOrderAndDifferentCase() {
    ExactTextsCaseSensitiveInAnyOrder texts = new ExactTextsCaseSensitiveInAnyOrder("one", "three", "two");
    assertThat(texts.test(asList(mockElement("One"), mockElement("Two"), mockElement("Three"))))
      .isFalse();
  }

  @Test
  void shouldNotMatchWithSmallerListSize() {
    ExactTextsCaseSensitiveInAnyOrder texts = new ExactTextsCaseSensitiveInAnyOrder("One", "Two");
    assertThat(texts.test(asList(mockElement("One"), mockElement("Two"), mockElement("Three"))))
      .isFalse();
  }

  @Test
  void shouldNotMatchWithBiggerListSize() {
    ExactTextsCaseSensitiveInAnyOrder texts = new ExactTextsCaseSensitiveInAnyOrder("One", "Two", "Three", "four");
    assertThat(texts.test(asList(mockElement("One"), mockElement("Two"), mockElement("Three"))))
      .isFalse();
  }

  @Test
  void shouldHaveCorrectToString() {
    assertThat(new ExactTextsCaseSensitiveInAnyOrder("One", "Two"))
      .hasToString("Exact texts case sensitive in any order [One, Two]");
  }
}
