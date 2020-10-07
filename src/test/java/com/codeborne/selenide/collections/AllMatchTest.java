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

final class AllMatchTest implements WithAssertions {
  private final SelenideElement element1 = mockElement("Test-One");
  private final SelenideElement element2 = mockElement("Test-Two");
  private final SelenideElement element3 = mockElement("Test-Three");
  private final WebElementsCollection collection = mockCollection("Collection description", element1, element2, element3);

  @Test
  void applyWithEmptyList() {
    assertThat(new AllMatch("Predicate description", it -> it.getText().equals("EmptyList"))
      .test(mockCollection("Collection description").getElements()))
      .isFalse();
  }

  @Test
  void applyWithNonMatchingPredicate() {
    assertThat(new AllMatch("Predicate description", it -> it.getText().equals("NotPresent"))
      .test(collection.getElements()))
      .isFalse();
  }

  @Test
  void applyWithMatchingPredicate() {
    assertThat(new AllMatch("Predicate description", it -> it.getText().contains("Test"))
      .test(collection.getElements()))
      .isTrue();
  }

  @Test
  void failOnMatcherError() {
    assertThatThrownBy(() ->
      new AllMatch("Predicate description", it -> it.getText().contains("Foo"))
        .fail(collection,
          collection.getElements(),
          new Exception("Exception message"), 10000))
      .isInstanceOf(MatcherError.class)
      .hasMessageStartingWith(String.format("Collection matcher error" +
        "%nExpected: all of elements to match [Predicate description] predicate" +
        "%nCollection: Collection description"));
  }

  @Test
  void failOnEmptyCollection() {
    assertThatThrownBy(() ->
      new AllMatch("Predicate description", it -> it.getText().equals("Test"))
        .fail(mockCollection("Collection description"),
          emptyList(),
          new Exception("Exception message"), 10000))
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void testToString() {
    assertThat(new AllMatch("Predicate description", it -> true))
      .hasToString("all match [Predicate description] predicate");
  }
}
