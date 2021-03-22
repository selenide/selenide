package com.codeborne.selenide.collections;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.DoesNotContainTextsError;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.CollectionSource;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockElement;
import static java.util.Arrays.asList;

public class ContainExactTextsCaseSensitiveTest implements WithAssertions {
  private final SelenideElement element1 = mockElement("Test-One");
  private final SelenideElement element2 = mockElement("Test-Two");
  private final SelenideElement element3 = mockElement("Test-Three");
  private final SelenideElement element4 = mockElement("Test-Four");
  private final SelenideElement element5 = mockElement("Test-Five");

  private final CollectionSource collection = mockCollection(
    "Collection with all 3 expected elements",
    element1, element2, element3);
  private final CollectionSource collectionMoreElements = mockCollection(
    "Collection with more elements that includes all the expected elements",
    element1, element2, element3, element4
  );
  private final CollectionSource collectionMoreElementsAndDuplicates = mockCollection(
    "Collection with more elements than expected " +
      "that contains duplicates of expected elements and includes all the expected elements",
    element1, element2, element3, element4, element2, element3
  );
  private final CollectionSource collectionWithoutElement = mockCollection(
    "Collection without one of the expected texts",
    element1, element2, element4
  );
  private final CollectionSource collectionMoreElementsWithoutTwoElements = mockCollection(
    "Collection with more elements than expected and without two of the expected texts",
    element1, element4, element5, element1, element4
  );
  private final CollectionSource collectionLessElementsWithoutElement = mockCollection(
    "Collection with less elements than expected and without one of the expected texts",
    element1, element2
  );
  private final CollectionSource emptyCollection = mockCollection("Empty collection");

  @Test
  void testExactCollection() {
    ContainExactTextsCaseSensitive expectedTexts =
      new ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three");

    assertThat(expectedTexts.test(collection.getElements()))
      .isTrue();
  }

  @Test
  void testCollectionUnordered() {
    ContainExactTextsCaseSensitive expectedTexts =
      new ContainExactTextsCaseSensitive("Test-Three", "Test-One", "Test-Two");

    assertThat(expectedTexts.test(collection.getElements()))
      .isTrue();
  }

  @Test
  void testCollectionUnorderedMoreElements() {
    ContainExactTextsCaseSensitive expectedTexts =
      new ContainExactTextsCaseSensitive("Test-Three", "Test-One", "Test-Two");

    assertThat(expectedTexts.test(collectionMoreElements.getElements()))
      .isTrue();
  }

  @Test
  void testCollectionUnorderedMoreElementsWithDuplicates() {
    ContainExactTextsCaseSensitive expectedTexts =
      new ContainExactTextsCaseSensitive("Test-Three", "Test-One", "Test-Two");

    assertThat(expectedTexts.test(collectionMoreElementsAndDuplicates.getElements()))
      .isTrue();
  }

  @Test
  void testCollectionWithoutElement() {
    ContainExactTextsCaseSensitive expectedTexts =
      new ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three");

    assertThat(expectedTexts.test(collectionWithoutElement.getElements()))
      .isFalse();

    assertThatThrownBy(() -> new ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three")
      .fail(collectionWithoutElement,
        collectionWithoutElement.getElements(),
        new Exception("Exception message"), 10_000))
      .isInstanceOf(DoesNotContainTextsError.class)
      .hasMessageContaining(
        String.format("The collection with text elements: %s%n" +
            "should contain all of the following text elements: %s%n" +
            "but could not find these elements: %s",
          ElementsCollection.texts(collectionWithoutElement.getElements()),
          asList("Test-One", "Test-Two", "Test-Three"),
          Collections.singletonList("Test-Three"))
      );
  }

  @Test
  void testCollectionMoreElementsWithoutSomeElements() {
    ContainExactTextsCaseSensitive expectedTexts =
      new ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three");

    assertThat(expectedTexts.test(collectionMoreElementsWithoutTwoElements.getElements()))
      .isFalse();

    assertThatThrownBy(() -> new ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three")
      .fail(collectionMoreElementsWithoutTwoElements,
        collectionMoreElementsWithoutTwoElements.getElements(),
        new Exception("Exception message"), 10_000))
      .isInstanceOf(DoesNotContainTextsError.class)
      .hasMessageContaining(
        String.format("The collection with text elements: %s%n" +
            "should contain all of the following text elements: %s%n" +
            "but could not find these elements: %s",
          ElementsCollection.texts(collectionMoreElementsWithoutTwoElements.getElements()),
          asList("Test-One", "Test-Two", "Test-Three"),
          asList("Test-Two", "Test-Three"))
      );
  }

  @Test
  void testCollectionLessElementsWithoutElement() {
    ContainExactTextsCaseSensitive expectedTexts =
      new ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three");

    assertThat(expectedTexts.test(collectionLessElementsWithoutElement.getElements()))
      .isFalse();

    assertThatThrownBy(() -> new ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three")
      .fail(collectionLessElementsWithoutElement,
        collectionLessElementsWithoutElement.getElements(),
        new Exception("Exception message"), 10_000))
      .isInstanceOf(DoesNotContainTextsError.class)
      .hasMessageContaining(
        String.format("The collection with text elements: %s%n" +
            "should contain all of the following text elements: %s%n" +
            "but could not find these elements: %s",
          ElementsCollection.texts(collectionLessElementsWithoutElement.getElements()),
          asList("Test-One", "Test-Two", "Test-Three"),
          Collections.singletonList("Test-Three"))
      );
  }

  @Test
  void testCollectionNullElements() {
    ContainExactTextsCaseSensitive expectedTexts =
      new ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three");

    assertThatThrownBy(() -> expectedTexts
      .fail(emptyCollection,
        null,
        new Exception("Exception message"), 10_000))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining(
        String.format("Element not found {Empty collection}" +
          "%nExpected: Contains exact texts case-sensitive [Test-One, Test-Two, Test-Three]")
      );
  }

  @Test
  void testCollectionEmpty() {
    ContainExactTextsCaseSensitive expectedTexts =
      new ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three");

    assertThatThrownBy(() -> expectedTexts
      .fail(emptyCollection,
        emptyCollection.getElements(),
        new Exception("Exception message"), 10_000))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining(
        String.format("Element not found {Empty collection}" +
          "%nExpected: Contains exact texts case-sensitive [Test-One, Test-Two, Test-Three]")
      );
  }

  @Test
  void testToString() {
    ContainExactTextsCaseSensitive expectedTexts =
      new ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three");

    assertThat(expectedTexts)
      .hasToString("Contains exact texts case-sensitive [Test-One, Test-Two, Test-Three]");
  }

  @Test
  void testApplyNull() {
    ContainExactTextsCaseSensitive expectedTexts =
      new ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three");

    assertThat(expectedTexts.applyNull())
      .isFalse();
  }
}
