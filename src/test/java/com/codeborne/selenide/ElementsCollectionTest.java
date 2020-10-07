package com.codeborne.selenide;

import com.codeborne.selenide.impl.SelenideElementIterator;
import com.codeborne.selenide.impl.SelenideElementListIterator;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.IntStream;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

final class ElementsCollectionTest implements WithAssertions {
  private final DriverStub driver = new DriverStub();
  private final WebElementsCollection source = mock(WebElementsCollection.class);
  private final WebElement element1 = element("h1");
  private final WebElement element2 = element("h2");
  private final WebElement element3 = element("h3");

  @BeforeEach
  void mockWebDriver() {
    when(source.driver()).thenReturn(driver);
  }

  @Test
  void shouldHaveSize() {
    ElementsCollection collection = spy(new ElementsCollection(source));
    when(source.getElements()).thenReturn(emptyList());

    collection.shouldHaveSize(0);

    when(source.getElements()).thenReturn(asList(element1, element2));
    collection.shouldHaveSize(2);

    verify(collection, never()).sleep(anyLong());
  }

  @Test
  void shouldBe() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(emptyList());

    collection.shouldBe(CollectionCondition.size(0));

    when(source.getElements()).thenReturn(asList(element1, element2));
    collection.shouldBe(CollectionCondition.size(2));
  }

  @Test
  void shouldWithErrorThrown() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(emptyList());

    assertThatThrownBy(() -> collection.should("Size", 1, CollectionCondition.size(1)))
      .isInstanceOf(Error.class);
  }

  @Test
  void shouldWithRuntimeException() {
    ElementsCollection collection = new ElementsCollection(source);
    doThrow(RuntimeException.class).when(source).getElements();

    assertThatThrownBy(() -> collection.should("Be size 1", 1, CollectionCondition.size(1)))
      .isInstanceOf(RuntimeException.class);
  }

  @Test
  void filter() {
    ElementsCollection filteredCollection = collection("Hello", "Mark").filter(text("Hello"));

    assertThat(filteredCollection).hasSize(1);
    assertThat(filteredCollection.get(0).getText()).isEqualTo("Hello");
  }

  @Test
  void filterBy() {
    ElementsCollection filteredCollection = collection("Hello", "Mark").filterBy(text("Hello"));

    assertThat(filteredCollection).hasSize(1);
    assertThat(filteredCollection.get(0).getText()).isEqualTo("Hello");
  }

  @Test
  void exclude() {
    ElementsCollection filteredCollection = collection("Hello", "Mark").exclude(text("Mark"));

    assertThat(filteredCollection).hasSize(1);
    assertThat(filteredCollection.get(0).getText()).isEqualTo("Hello");
  }

  @Test
  void excludeWith() {
    ElementsCollection filteredCollection = collection("Hello", "Mark").excludeWith(text("Mark"));

    assertThat(filteredCollection).hasSize(1);
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
  void texts() {
    ElementsCollection collection = collection("Hello", "Mark");
    List<String> elementsTexts = collection.texts();
    assertThat(elementsTexts).isEqualTo(asList("Hello", "Mark"));
  }

  @Test
  void staticGetTexts() {
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    List<String> elementsTexts = ElementsCollection.texts(asList(element1, element2));
    List<String> expectedTexts = asList("Hello", "Mark");
    assertThat(elementsTexts)
      .isEqualTo(expectedTexts);
  }

  @Test
  void staticGetTextsWithWebDriverException() {
    doThrow(new WebDriverException("Failed to fetch elements")).when(element1).getText();
    when(element2.getText()).thenReturn("Mark");
    List<String> elementsTexts = ElementsCollection.texts(asList(element1, element2));
    List<String> expectedTexts = asList("org.openqa.selenium.WebDriverException: Failed to fetch elements", "Mark");
    assertThat(elementsTexts)
      .hasSameSizeAs(expectedTexts);
    IntStream.range(0, expectedTexts.size())
      .forEach(index -> assertThat(elementsTexts.get(index))
        .contains(expectedTexts.get(index)));
  }

  @Test
  void elementsToStringOnNullCollection() {
    assertThat(ElementsCollection.elementsToString(driver, null)).isEqualTo("[not loaded yet...]");
  }

  @Test
  void firstMethod() {
    assertThat(collection("Hello", "Mark").first().getText()).isEqualTo("Hello");
  }

  @Test
  void firstNElementsMethod() {
    ElementsCollection collection = collection("Hello", "Mark", "Twen");
    ElementsCollection firstTwoElements = collection.first(2);
    assertThat(firstTwoElements)
      .hasSize(2);
    assertThat(firstTwoElements)
      .extracting(SelenideElement::getText)
      .contains("Hello", "Mark");
  }

  @Test
  void lastMethod() {
    assertThat(collection("Hello", "Mark").last().getText()).isEqualTo("Mark");
  }

  @Test
  void lastNElementsMethod() {
    ElementsCollection collection = collection("Hello", "Mark", "Twen");
    ElementsCollection firstTwoElements = collection.last(2);
    assertThat(firstTwoElements)
      .hasSize(2);
    assertThat(firstTwoElements)
      .extracting(SelenideElement::getText)
      .contains("Mark", "Twen");
  }

  @Test
  void iteratorMethod() {
    when(source.getElements()).thenReturn(asList(element1, element2, element3));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    when(element3.getText()).thenReturn("Twen");
    Iterator<SelenideElement> iterator = new ElementsCollection(source).iterator();
    assertThat(iterator).isNotNull();
    assertThat(iterator).isInstanceOf(SelenideElementIterator.class);
    assertThat(iterator.hasNext()).isTrue();
  }

  @Test
  void iteratorListMethod() {
    when(source.getElements()).thenReturn(asList(element1, element2, element3));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    when(element3.getText()).thenReturn("Twen");
    ListIterator<SelenideElement> iteratorList = new ElementsCollection(source).listIterator(1);
    assertThat(iteratorList).isNotNull();
    assertThat(iteratorList).isInstanceOf(SelenideElementListIterator.class);
    assertThat(iteratorList.hasNext()).isTrue();
  }

  @Test
  void doesNotWait_ifConditionAlreadyMatches() {
    WebElementsCollection source = mock(WebElementsCollection.class);
    when(source.driver()).thenReturn(driver);
    ElementsCollection collection = spy(new ElementsCollection(source));
    when(source.getElements()).thenReturn(asList(element1, element2));

    collection.shouldHave(size(2));
    verify(collection, never()).sleep(anyLong());
  }

  @Test
  void doesNotWait_ifJavascriptExceptionHappened() {
    WebElementsCollection source = mock(WebElementsCollection.class);
    when(source.driver()).thenReturn(driver);
    ElementsCollection collection = spy(new ElementsCollection(source));
    when(source.getElements()).thenThrow(new JavascriptException("ReferenceError: Sizzle is not defined"));

    assertThatThrownBy(() -> collection.shouldHave(size(0))).isInstanceOf(JavascriptException.class);

    verify(collection, never()).sleep(anyLong());
  }

  @Test
  void sleepsAsLessAsPossible_untilConditionGetsMatched() {
    ElementsCollection collection = spy(new ElementsCollection(source));
    when(source.getElements()).thenReturn(
      singletonList(element1),
      asList(element1, element2),
      asList(element1, element2, element2)
    );

    collection.shouldHave(size(3));
    verify(collection, times(2)).sleep(anyLong());
  }

  @Test
  void toStringFetchedCollectionFromWebdriverIfNotFetchedYet() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    assertThat(collection)
      .hasToString(String.format("[%n\t<h1></h1>,%n\t<h2></h2>%n]"));
  }

  @Test
  void toStringPrintsErrorIfFailedToFetchElements() {
    when(source.getElements()).thenThrow(new WebDriverException("Failed to fetch elements"));
    assertThat(new ElementsCollection(source))
      .hasToString("[WebDriverException: Failed to fetch elements]");
  }

  @Test
  void toArray() {
    when(source.getElements()).thenReturn(asList(element1, element2));
    assertThat(new ElementsCollection(source).toArray()).hasOnlyElementsOfType(SelenideElement.class);
  }

  private WebElement element(String tag) {
    WebElement element = mock(WebElement.class);
    when(element.getTagName()).thenReturn(tag);
    when(element.isDisplayed()).thenReturn(true);
    when(element.isEnabled()).thenReturn(true);
    return element;
  }

  private ElementsCollection collection(String first, String second) {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(source.getElement(0)).thenReturn(element1);
    when(source.getElement(1)).thenReturn(element2);
    when(element1.getText()).thenReturn(first);
    when(element2.getText()).thenReturn(second);
    return collection;
  }

  private ElementsCollection collection(String first, String second, String third) {
    ElementsCollection collection = new ElementsCollection(source);
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
