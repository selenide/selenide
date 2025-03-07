package com.codeborne.selenide;

import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.impl.CollectionSource;
import com.codeborne.selenide.impl.SelenideElementIterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Mocks.elementWithAttribute;
import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockWebElement;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class ElementsCollectionTest {
  private final DriverStub driver = new DriverStub();
  private final CollectionSource source = mock();
  private final WebElement element1 = mockWebElement("h1", "First");
  private final WebElement element2 = mockWebElement("h2", "Second");
  private final WebElement element3 = mockWebElement("h3", "Third");
  private final ElementsCollection collection = spy(new ElementsCollection(source));

  @BeforeEach
  void mockWebDriver() {
    when(source.driver()).thenReturn(driver);
  }

  @Test
  void shouldHaveSize() {
    when(source.getElements()).thenReturn(emptyList());

    collection.shouldHave(size(0));

    verify(collection, never()).sleep(any());
  }

  @Test
  void shouldHaveSize_doesNotWait_ifSizeIsAlreadyExpected() {
    when(source.getElements()).thenReturn(asList(element1, element2));

    collection.shouldHave(size(2));

    verify(collection, never()).sleep(any());
  }

  @Test
  void shouldBe() {
    when(source.getElements()).thenReturn(emptyList());
    collection.shouldBe(CollectionCondition.size(0));
  }

  @Test
  void shouldBe_size() {
    when(source.getElements()).thenReturn(asList(element1, element2));
    collection.shouldBe(CollectionCondition.size(2));
  }

  @Test
  void shouldWithErrorThrown() {
    when(source.getElements()).thenReturn(emptyList());

    assertThatThrownBy(() -> collection.should("Size", Duration.ofMillis(1), CollectionCondition.size(1)))
      .isInstanceOf(ListSizeMismatch.class);
  }

  @Test
  void shouldWithRuntimeException() {
    doThrow(new IllegalArgumentException("Oops")).when(source).getElements();

    assertThatThrownBy(() -> collection.should("Be size 1", Duration.ofMillis(1), CollectionCondition.size(1)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Oops");
  }

  @Test
  void filter() {
    ElementsCollection filteredCollection = collection("Hello", "Mark").filter(text("Hello"));

    assertThat(filteredCollection.size()).isEqualTo(1);
    assertThat(filteredCollection.get(0).getText()).isEqualTo("Hello");
  }

  @Test
  void filterBy() {
    ElementsCollection filteredCollection = collection("Hello", "Mark").filterBy(text("Hello"));

    assertThat(filteredCollection.size()).isEqualTo(1);
    assertThat(filteredCollection.get(0).getText()).isEqualTo("Hello");
  }

  @Test
  void exclude() {
    var filteredCollection = collection("Hello", "Mark").exclude(text("Mark"));

    assertThat(filteredCollection.size()).isEqualTo(1);
    assertThat(filteredCollection.get(0).getText()).isEqualTo("Hello");
  }

  @Test
  void excludeWith() {
    var filteredCollection = collection("Hello", "Mark").excludeWith(text("Mark"));

    assertThat(filteredCollection.size()).isEqualTo(1);
    assertThat(filteredCollection.get(0).getText()).isEqualTo("Hello");
  }

  @Test
  void find() {
    SelenideElement foundElement = collection("Hello", "Mark").find(text("Hello"));
    assertThat(foundElement.getText()).isEqualTo("Hello");
  }

  @Test
  void findBy() {
    SelenideElement foundElement = collection("Hello", "Mark").findBy(text("Hello"));
    assertThat(foundElement.getText()).isEqualTo("Hello");
  }

  @Test
  void attributes() {
    ElementsCollection collection = mockCollection(
      elementWithAttribute("data-test-id", "10001"),
      elementWithAttribute("data-test-id", "20002")
    );
    var elementsTexts = collection.attributes("data-test-id");
    assertThat(elementsTexts).containsExactly("10001", "20002");
  }

  @Test
  void texts() {
    ElementsCollection collection = collection("Hello", "Mark");
    List<String> elementsTexts = collection.texts();
    assertThat(elementsTexts).containsExactly("Hello", "Mark");
  }

  @Test
  void firstMethod() {
    assertThat(collection("Hello", "Mark").first().getText()).isEqualTo("Hello");
  }

  @Test
  void firstNElementsMethod() {
    ElementsCollection collection = collection("Hello", "Mark", "Twen");
    var firstTwoElements = collection.first(2);
    assertThat(firstTwoElements.texts()).isEqualTo(List.of("Hello", "Mark"));
    assertThat(firstTwoElements.size()).isEqualTo(2);
    assertThat(firstTwoElements.get(0).getText()).isEqualTo("Hello");
    assertThat(firstTwoElements.get(1).getText()).isEqualTo("Mark");
  }

  @Test
  void lastMethod() {
    assertThat(collection("Hello", "Mark").last().getText()).isEqualTo("Mark");
  }

  @Test
  void lastNElementsMethod() {
    ElementsCollection collection = collection("Hello", "Mark", "Twen");
    var firstTwoElements = collection.last(2);
    assertThat(firstTwoElements.texts()).isEqualTo(List.of("Mark", "Twen"));
  }

  @Test
  void iteratorMethod() {
    when(source.getElements()).thenReturn(asList(element1, element2, element3));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    when(element3.getText()).thenReturn("Twen");
    Iterator<SelenideElement> iterator = new ElementsCollection(source).asFixedIterable().iterator();
    assertThat(iterator).isNotNull();
    assertThat(iterator).isInstanceOf(SelenideElementIterator.class);
    assertThat(iterator.hasNext()).isTrue();
  }

  @Test
  void doesNotWait_ifConditionAlreadyMatches() {
    when(source.getElements()).thenReturn(asList(element1, element2));

    collection.shouldHave(size(2));
    verify(collection, never()).sleep(any());
  }

  @Test
  void doesNotWait_ifJavascriptExceptionHappened() {
    when(source.getElements()).thenThrow(new JavascriptException("ReferenceError: Sizzle is not defined"));

    assertThatThrownBy(() -> collection.shouldHave(size(0))).isInstanceOf(JavascriptException.class);

    verify(collection, never()).sleep(any());
  }

  @Test
  void sleepsAsLessAsPossible_untilConditionGetsMatched() {
    when(source.getElements()).thenReturn(
      singletonList(element1),
      asList(element1, element2),
      asList(element1, element2, element2)
    );

    collection.shouldHave(size(3));
    verify(collection, times(2)).sleep(any());
  }

  private static ElementsCollection collection(String firstText, String secondText) {
    WebElement element1 = mockWebElement("h1", firstText);
    WebElement element2 = mockWebElement("h2", secondText);
    return mockCollection(element1, element2);
  }

  private ElementsCollection collection(String first, String second, String third) {
    when(source.getElements()).thenReturn(asList(element1, element2, element3));
    when(source.getElement(0)).thenReturn(element1);
    when(source.getElement(1)).thenReturn(element2);
    when(source.getElement(2)).thenReturn(element3);
    when(element1.getText()).thenReturn(first);
    when(element2.getText()).thenReturn(second);
    when(element3.getText()).thenReturn(third);
    return collection;
  }
}
