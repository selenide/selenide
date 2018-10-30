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

import java.util.*;
import java.util.stream.IntStream;

import static com.codeborne.selenide.CollectionCondition.size;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ElementsCollectionTest implements WithAssertions {
  private DriverStub driver = new DriverStub();
  private WebElementsCollection source = mock(WebElementsCollection.class);
  private WebElement element1 = element("h1");
  private WebElement element2 = element("h2");
  private WebElement element3 = element("h3");

  @BeforeEach
  final void mockWebDriver() {
    when(source.driver()).thenReturn(driver);
  }

  @Test
  void testShouldHaveSize() {
    ElementsCollection collection = spy(new ElementsCollection(source));
    when(source.getElements()).thenReturn(Collections.emptyList());

    collection.shouldHaveSize(0);

    when(source.getElements()).thenReturn(asList(element1, element2));
    collection.shouldHaveSize(2);

    verify(collection, never()).sleep(anyLong());
  }

  @Test
  void testShouldBe() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(Collections.emptyList());

    collection.shouldBe(CollectionCondition.size(0));

    when(source.getElements()).thenReturn(asList(element1, element2));
    collection.shouldBe(CollectionCondition.size(2));
  }

  @Test
  void testShouldWithErrorThrown() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(Collections.emptyList());

    assertThatThrownBy(() -> collection.should("Size", 1, CollectionCondition.size(1)))
      .isInstanceOf(Error.class);
  }

  @Test
  void testShouldWithRuntimeException() {
    ElementsCollection collection = new ElementsCollection(source);
    doThrow(RuntimeException.class).when(source).getElements();

    assertThatThrownBy(() -> collection.should("Be size 1", 1, CollectionCondition.size(1)))
      .isInstanceOf(RuntimeException.class);
  }

  @Test
  void testFilter() {
    checkFilterMethod(false);
  }

  private void checkFilterMethod(boolean useBy) {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    ElementsCollection filteredCollection = useBy ?
      collection.filterBy(Condition.text("Hello")) :
      collection.filter(Condition.text("Hello"));
    assertThat(filteredCollection)
      .hasSize(1);
    assertThat(filteredCollection)
      .extracting(SelenideElement::getText)
      .contains("Hello");
  }

  @Test
  void testFilterBy() {
    checkFilterMethod(true);
  }

  @Test
  void testExclude() {
    checkExcludeMethod(false);
  }

  private void checkExcludeMethod(boolean useWith) {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    ElementsCollection filteredCollection = useWith ?
      collection.excludeWith(Condition.text("Mark")) :
      collection.exclude(Condition.text("Mark"));
    assertThat(filteredCollection)
      .hasSize(1);
    assertThat(filteredCollection)
      .extracting(SelenideElement::getText)
      .contains("Hello");
  }

  @Test
  void testExcludeWith() {
    checkExcludeMethod(true);
  }

  @Test
  void testFind() {
    checkFindMethod(false);
  }

  private void checkFindMethod(boolean useBy) {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    Condition condition = Condition.text("Hello");
    SelenideElement foundElement = useBy ? collection.findBy(condition) : collection.find(condition);
    assertThat(foundElement.getText())
      .isEqualTo("Hello");
  }

  @Test
  void testFindBy() {
    checkFindMethod(true);
  }

  @Test
  void testTexts() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    List<String> elementsTexts = collection.texts();
    assertThat(elementsTexts)
      .contains("Hello", "Mark");
  }

  @Test
  void testStaticGetTexts() {
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    List elementsTexts = ElementsCollection.texts(asList(element1, element2));
    List expectedTexts = Arrays.asList("Hello", "Mark");
    assertThat(elementsTexts)
      .isEqualTo(expectedTexts);
  }

  @Test
  void testStaticGetTextsWithWebDriverException() {
    doThrow(new WebDriverException("Failed to fetch elements")).when(element1).getText();
    when(element2.getText()).thenReturn("Mark");
    List<String> elementsTexts = ElementsCollection.texts(asList(element1, element2));
    List<String> expectedTexts = Arrays.asList("org.openqa.selenium.WebDriverException: Failed to fetch elements", "Mark");
    assertThat(elementsTexts)
      .hasSameSizeAs(expectedTexts);
    IntStream.range(0, expectedTexts.size())
      .forEach(index -> assertThat(elementsTexts.get(index))
        .contains(expectedTexts.get(index)));
  }

  @Test
  void testElementsToStringOnNullCollection() {
    assertThat(ElementsCollection.elementsToString(null, null))
      .isEqualTo("[not loaded yet...]");
  }

  @Test
  void testFirstMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    assertThat(collection.first().getText())
      .isEqualTo("Hello");
  }

  @Test
  void testFirstNElementsMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2, element3));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    when(element3.getText()).thenReturn("Twen");
    ElementsCollection firstTwoElements = collection.first(2);
    assertThat(firstTwoElements)
      .hasSize(2);
    assertThat(firstTwoElements)
      .extracting(SelenideElement::getText)
      .contains("Hello", "Mark");
  }

  @Test
  void testLastMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    assertThat(collection.last().getText())
      .isEqualTo("Mark");
  }

  @Test
  void testLastNElementsMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2, element3));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    when(element3.getText()).thenReturn("Twen");
    ElementsCollection firstTwoElements = collection.last(2);
    assertThat(firstTwoElements)
      .hasSize(2);
    assertThat(firstTwoElements)
      .extracting(SelenideElement::getText)
      .contains("Mark", "Twen");
  }

  @Test
  void testIteratorMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2, element3));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    when(element3.getText()).thenReturn("Twen");
    Iterator<SelenideElement> iterator = collection.iterator();
    assertThat(iterator)
      .isNotNull();
    assertThat(iterator)
      .isInstanceOf(SelenideElementIterator.class);
    assertThat(iterator.hasNext())
      .isTrue();
  }

  @Test
  void testIteratorListMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2, element3));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    when(element3.getText()).thenReturn("Twen");
    ListIterator<SelenideElement> iteratorList = collection.listIterator(1);
    assertThat(iteratorList)
      .isNotNull();
    assertThat(iteratorList)
      .isInstanceOf(SelenideElementListIterator.class);
    assertThat(iteratorList.hasNext())
      .isTrue();
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
      Collections.singletonList(element1),
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
      .hasToString("[\n\t<h1></h1>,\n\t<h2></h2>\n]");
  }

  @Test
  void toStringPrintsErrorIfFailedToFetchElements() {
    when(source.getElements()).thenThrow(new WebDriverException("Failed to fetch elements"));
    assertThat(new ElementsCollection(source))
      .hasToString("[WebDriverException: Failed to fetch elements]");
  }

  @Test
  void toArray() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    assertThat(collection.toArray()).hasOnlyElementsOfType(SelenideElement.class);
  }

  private WebElement element(String tag) {
    WebElement element = mock(WebElement.class);
    when(element.getTagName()).thenReturn(tag);
    when(element.isDisplayed()).thenReturn(true);
    when(element.isEnabled()).thenReturn(true);
    return element;
  }
}
