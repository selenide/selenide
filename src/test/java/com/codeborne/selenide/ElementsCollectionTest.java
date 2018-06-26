package com.codeborne.selenide;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.codeborne.selenide.extension.MockWebDriverExtension;
import com.codeborne.selenide.impl.SelenideElementIterator;
import com.codeborne.selenide.impl.SelenideElementListIterator;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Configuration.browser;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockWebDriverExtension.class)
class ElementsCollectionTest {
  private WebElementsCollection source = mock(WebElementsCollection.class);
  private WebElement element1 = element("h1");
  private WebElement element2 = element("h2");
  private WebElement element3 = element("h3");

  @BeforeEach
  final void mockWebDriver() {
    browser = null;
  }

  @Test
  void testShouldHaveSize() {
    ElementsCollection collection = spy(new ElementsCollection(source));
    when(source.getElements()).thenReturn(Collections.emptyList());

    collection.shouldHaveSize(0);

    when(source.getActualElements()).thenReturn(asList(element1, element2));
    collection.shouldHaveSize(2);

    verify(collection, never()).sleep(anyLong());
  }

  @Test
  void testShouldBe() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(Collections.emptyList());

    collection.shouldBe(CollectionCondition.size(0));

    when(source.getActualElements()).thenReturn(asList(element1, element2));
    collection.shouldBe(CollectionCondition.size(2));
  }

  @Test
  void testShouldWithErrorThrown() {
    Assertions.assertThrows(Error.class, () -> {
      ElementsCollection collection = new ElementsCollection(source);
      when(source.getElements()).thenReturn(Collections.emptyList());
      collection.should("Size", CollectionCondition.size(1));
    });
  }

  @Test
  void testShouldWithRuntimeException() {
    Assertions.assertThrows(RuntimeException.class, () -> {
      ElementsCollection collection = new ElementsCollection(source);
      doThrow(RuntimeException.class).when(source).getActualElements();
      collection.should("Be size 1", CollectionCondition.size(1));
    });
  }

  @Test
  void testFilter() {
    checkFilterMethod(false);
  }

  private void checkFilterMethod(boolean useBy) {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getActualElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    ElementsCollection filteredCollection = useBy ?
      collection.filterBy(Condition.text("Hello")) :
      collection.filter(Condition.text("Hello"));
    Assertions.assertEquals(1, filteredCollection.size());
    Assertions.assertEquals("Hello", filteredCollection.get(0).getText());
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
    when(source.getActualElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    ElementsCollection filteredCollection = useWith ?
      collection.excludeWith(Condition.text("Mark")) :
      collection.exclude(Condition.text("Mark"));
    Assertions.assertEquals(1, filteredCollection.size());
    Assertions.assertEquals("Hello", filteredCollection.get(0).getText());
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
    when(source.getActualElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    Condition condition = Condition.text("Hello");
    SelenideElement foundElement = useBy ? collection.findBy(condition) : collection.find(condition);
    Assertions.assertEquals("Hello", foundElement.getText());
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
    Assertions.assertEquals(asList("Hello", "Mark"), elementsTexts);
  }

  @Test
  void testStaticGetTexts() {
    when(source.getActualElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    String[] elementsTexts = ElementsCollection.getTexts(asList(element1, element2));
    String[] expectedTexts = new String[]{"Hello", "Mark"};
    Assertions.assertEquals(expectedTexts.length, elementsTexts.length);
    for (int index = 0; index < expectedTexts.length; index++) {
      Assertions.assertEquals(expectedTexts[index], elementsTexts[index]);
    }
  }

  @Test
  void testStaticGetTextsWithWebDriverException() {
    doThrow(new WebDriverException("Failed to fetch elements")).when(element1).getText();
    when(element2.getText()).thenReturn("Mark");
    String[] elementsTexts = ElementsCollection.getTexts(asList(element1, element2));
    String[] expectedTexts = new String[]{"org.openqa.selenium.WebDriverException: Failed to fetch elements", "Mark"};
    Assertions.assertEquals(expectedTexts.length, elementsTexts.length);
    for (int index = 0; index < expectedTexts.length; index++) {
      Assertions.assertTrue(elementsTexts[index].contains(expectedTexts[index]));
    }
  }

  @Test
  void testElementsToStringOnNullCollection() {
    Assertions.assertEquals("[not loaded yet...]", ElementsCollection.elementsToString(null));
  }

  @Test
  void testFirstMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    Assertions.assertEquals("Hello", collection.first().getText());
  }

  @Test
  void testFirstNElementsMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getActualElements()).thenReturn(asList(element1, element2, element3));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    when(element3.getText()).thenReturn("Twen");
    ElementsCollection firstTwoElements = collection.first(2);
    Assertions.assertEquals(2, firstTwoElements.size());
    Assertions.assertEquals("Hello", firstTwoElements.get(0).getText());
    Assertions.assertEquals("Mark", firstTwoElements.get(1).getText());
  }

  @Test
  void testLastMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    Assertions.assertEquals("Mark", collection.last().getText());
  }

  @Test
  void testLasttNElementsMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getActualElements()).thenReturn(asList(element1, element2, element3));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    when(element3.getText()).thenReturn("Twen");
    ElementsCollection firstTwoElements = collection.last(2);
    Assertions.assertEquals(2, firstTwoElements.size());
    Assertions.assertEquals("Mark", firstTwoElements.get(0).getText());
    Assertions.assertEquals("Twen", firstTwoElements.get(1).getText());
  }

  @Test
  void testIteratorMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getActualElements()).thenReturn(asList(element1, element2, element3));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    when(element3.getText()).thenReturn("Twen");
    Iterator<SelenideElement> iterator = collection.iterator();
    Assertions.assertNotNull(iterator);
    Assertions.assertTrue(iterator instanceof SelenideElementIterator);
    Assertions.assertTrue(iterator.hasNext());
  }

  @Test
  void testIteratorListMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getActualElements()).thenReturn(asList(element1, element2, element3));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    when(element3.getText()).thenReturn("Twen");
    ListIterator<SelenideElement> iteratorList = collection.listIterator(1);
    Assertions.assertNotNull(iteratorList);
    Assertions.assertTrue(iteratorList instanceof SelenideElementListIterator);
    Assertions.assertTrue(iteratorList.hasNext());
  }

  @Test
  void doesNotWait_ifConditionAlreadyMatches() {
    WebElementsCollection source = mock(WebElementsCollection.class);
    ElementsCollection collection = spy(new ElementsCollection(source));
    when(source.getActualElements()).thenReturn(asList(element1, element2));

    collection.shouldHave(size(2));
    verify(collection, never()).sleep(anyLong());
  }

  @Test
  void sleepsAsLessAsPossible_untilConditionGetsMatched() {
    ElementsCollection collection = spy(new ElementsCollection(source));
    when(source.getActualElements()).thenReturn(
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
    Assertions.assertEquals("[\n\t<h1></h1>,\n\t<h2></h2>\n]", collection.toString());
  }

  @Test
  void toStringPrintsErrorIfFailedToFetchElements() {
    when(source.getElements()).thenThrow(new WebDriverException("Failed to fetch elements"));
    Assertions.assertEquals("[WebDriverException: Failed to fetch elements]", new ElementsCollection(source).toString());
  }

  private WebElement element(String tag) {
    WebElement element = mock(WebElement.class);
    when(element.getTagName()).thenReturn(tag);
    when(element.isDisplayed()).thenReturn(true);
    when(element.isEnabled()).thenReturn(true);
    return element;
  }
}
