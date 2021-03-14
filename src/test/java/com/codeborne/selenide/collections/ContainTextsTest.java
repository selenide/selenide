package com.codeborne.selenide.collections;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.TextsMismatch;
import com.codeborne.selenide.ex.TextsSizeMismatch;
import com.codeborne.selenide.impl.CollectionSource;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockElement;
import static java.util.Arrays.asList;

public class ContainTextsTest implements WithAssertions {
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
  private final CollectionSource collectionMoreElementsWithoutElement = mockCollection(
    "Collection with more elements than expected and without one of the expected texts",
    element1, element2, element4, element5
  );
  private final CollectionSource collectionLessElementsWithoutElement = mockCollection(
    "Collection with less elements than expected and without one of the expected texts",
    element1, element2
  );
  private final CollectionSource emptyCollection = mockCollection("Empty collection");

  @Test
  void testExactCollection() {
    ContainTexts expectedTexts = new ContainTexts("Test-One", "Test-Two", "Test-Three");

    assertThat(expectedTexts.test(collection.getElements()))
      .isTrue();
  }

  @Test
  void testCollectionUnordered() {
    ContainTexts expectedTexts = new ContainTexts("Test-Three", "Test-One", "Test-Two");

    assertThat(expectedTexts.test(collection.getElements()))
      .isTrue();
  }

  @Test
  void testCollectionUnorderedMoreElements() {
    ContainTexts expectedTexts = new ContainTexts("Test-Three", "Test-One", "Test-Two");

    assertThat(expectedTexts.test(collectionMoreElements.getElements()))
      .isTrue();
  }

  @Test
  void testCollectionUnorderedMoreElementsWithDuplicates() {
    ContainTexts expectedTexts = new ContainTexts("Test-Three", "Test-One", "Test-Two");

    assertThat(expectedTexts.test(collectionMoreElementsAndDuplicates.getElements()))
      .isTrue();
  }

  @Test
  void testCollectionWithoutElement() {
    ContainTexts expectedTexts = new ContainTexts("Test-One", "Test-Two", "Test-Three");

    assertThat(expectedTexts.test(collectionWithoutElement.getElements()))
      .isFalse();

    assertThatThrownBy(() -> new ContainTexts("Test-One", "Test-Two", "Test-Three")
      .fail(collectionWithoutElement,
        collectionWithoutElement.getElements(),
        new Exception("Exception message"), 10_000))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageContaining(
        String.format("Texts mismatch" +
            "%nActual: %s" +
            "%nExpected: %s",
          ElementsCollection.texts(collectionWithoutElement.getElements()),
          asList("Test-One", "Test-Two", "Test-Three"))
      );
  }

  @Test
  void testCollectionMoreElementsWithoutElement() {
    ContainTexts expectedTexts = new ContainTexts("Test-One", "Test-Two", "Test-Three");

    assertThat(expectedTexts.test(collectionMoreElementsWithoutElement.getElements()))
      .isFalse();

    assertThatThrownBy(() -> new ContainTexts("Test-One", "Test-Two", "Test-Three")
      .fail(collectionMoreElementsWithoutElement,
        collectionMoreElementsWithoutElement.getElements(),
        new Exception("Exception message"), 10_000))
      .isInstanceOf(TextsMismatch.class)
      .hasMessageContaining(
        String.format("Texts mismatch" +
            "%nActual: %s" +
            "%nExpected: %s",
          ElementsCollection.texts(collectionMoreElementsWithoutElement.getElements()),
          asList("Test-One", "Test-Two", "Test-Three"))
      );
  }

  @Test
  void testCollectionLessElementsWithoutElement() {
    ContainTexts expectedTexts = new ContainTexts("Test-One", "Test-Two", "Test-Three");

    assertThat(expectedTexts.test(collectionLessElementsWithoutElement.getElements()))
      .isFalse();

    assertThatThrownBy(() -> new ContainTexts("Test-One", "Test-Two", "Test-Three")
      .fail(collectionLessElementsWithoutElement,
        collectionLessElementsWithoutElement.getElements(),
        new Exception("Exception message"), 10_000))
      .isInstanceOf(TextsSizeMismatch.class)
      .hasMessageContaining(
        String.format("Texts size mismatch" +
            "%nActual: %s, List size: %s" +
            "%nExpected: %s, List size: %s",
          ElementsCollection.texts(collectionLessElementsWithoutElement.getElements()), "2",
          asList("Test-One", "Test-Two", "Test-Three"), "3")
      );
  }

  @Test
  void testCollectionNullElements() {
    ContainTexts expectedTexts = new ContainTexts("Test-One", "Test-Two", "Test-Three");

    assertThatThrownBy(() -> expectedTexts
      .fail(emptyCollection,
        null,
        new Exception("Exception message"), 10_000))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining(
        String.format("Element not found {Empty collection}" +
          "%nExpected: Contains texts [Test-One, Test-Two, Test-Three]")
      );
  }

  @Test
  void testCollectionEmpty() {
    ContainTexts expectedTexts = new ContainTexts("Test-One", "Test-Two", "Test-Three");

    assertThatThrownBy(() -> expectedTexts
      .fail(emptyCollection,
        emptyCollection.getElements(),
        new Exception("Exception message"), 10_000))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining(
        String.format("Element not found {Empty collection}" +
          "%nExpected: Contains texts [Test-One, Test-Two, Test-Three]")
      );
  }

  @Test
  void testToString() {
    ContainTexts expectedTexts = new ContainTexts("Test-One", "Test-Two", "Test-Three");

    assertThat(expectedTexts)
      .hasToString("Contains texts [Test-One, Test-Two, Test-Three]");
  }

  @Test
  void testApplyNull() {
    ContainTexts expectedTexts = new ContainTexts("Test-One", "Test-Two", "Test-Three");

    assertThat(expectedTexts.applyNull())
      .isFalse();
  }
}
