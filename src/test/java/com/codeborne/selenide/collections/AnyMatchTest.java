package com.codeborne.selenide.collections;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.MatcherError;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockElement;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

final class AnyMatchTest implements WithAssertions {
  private final SelenideElement element1 = mockElement("Hello");
  private final SelenideElement element2 = mockElement("World");

  @Test
  void applyWithEmptyList() {
    assertThat(new AnyMatch("Predicate description", it -> it.getText().equals("World"))
      .test(mockCollection("Collection description").getElements()))
      .isFalse();
  }

  @Test
  void applyWithNonMatchingPredicate() {
    assertThat(new AnyMatch("Predicate description", it -> it.getText().equals("World"))
      .test(singletonList(element1)))
      .isFalse();
  }

  @Test
  void applyWithMatchingPredicate() {
    WebElementsCollection collection = mockCollection("Collection description", element1, element2);

    assertThat(new AnyMatch("Predicate description", it -> it.getText().equals("World"))
      .test(collection.getElements()))
      .isTrue();
  }

  @Test
  void failOnMatcherError() {
    WebElementsCollection collection = mockCollection("Collection description");

    assertThatThrownBy(() ->
      new AnyMatch("Predicate description", it -> it.getText().equals("World"))
        .fail(collection,
          singletonList(element1),
          new Exception("Exception message"), 10000))
      .isInstanceOf(MatcherError.class)
      .hasMessageStartingWith(String.format("Collection matcher error" +
        "%nExpected: any of elements to match [Predicate description] predicate" +
        "%nCollection: Collection description"));
  }

  @Test
  void failOnEmptyCollection() {
    assertThatThrownBy(() ->
      new AnyMatch("Predicate description", it -> it.getText().equals("World"))
        .fail(mockCollection("Collection description"),
          emptyList(),
          new Exception("Exception message"), 10000))
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void testToString() {
    assertThat(new AnyMatch("Predicate description", it -> true))
      .hasToString("any match [Predicate description] predicate");
  }
}
