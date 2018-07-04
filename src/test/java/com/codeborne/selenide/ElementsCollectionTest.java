package com.codeborne.selenide;

import com.codeborne.selenide.impl.SelenideElementIterator;
import com.codeborne.selenide.impl.SelenideElementListIterator;
import com.codeborne.selenide.impl.WebElementsCollection;
import com.codeborne.selenide.rules.MockWebdriverContainer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Configuration.browser;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ElementsCollectionTest {
  @Rule
  public MockWebdriverContainer mockWebdriverContainer = new MockWebdriverContainer();

  private WebElementsCollection source = mock(WebElementsCollection.class);
  private WebElement element1 = element("h1");
  private WebElement element2 = element("h2");
  private WebElement element3 = element("h3");

  @Before
  public final void mockWebDriver() {
    browser = null;
  }

  @Test
  public void testShouldHaveSize() {
    ElementsCollection collection = spy(new ElementsCollection(source));
    when(source.getElements()).thenReturn(Collections.emptyList());

    collection.shouldHaveSize(0);

    when(source.getElements()).thenReturn(asList(element1, element2));
    collection.shouldHaveSize(2);

    verify(collection, never()).sleep(anyLong());
  }

  @Test
  public void testShouldBe() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(Collections.emptyList());

    collection.shouldBe(CollectionCondition.size(0));

    when(source.getElements()).thenReturn(asList(element1, element2));
    collection.shouldBe(CollectionCondition.size(2));
  }

  @Test(expected = Error.class)
  public void testShouldWithErrorThrown() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(Collections.emptyList());

    collection.should("Size", CollectionCondition.size(1));
  }

  @Test(expected = RuntimeException.class)
  public void testShouldWithRuntimeException() {
    ElementsCollection collection = new ElementsCollection(source);
    doThrow(RuntimeException.class).when(source).getElements();
    collection.should("Be size 1", CollectionCondition.size(1));
  }

  @Test
  public void testFilter() {
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
    assertEquals(1, filteredCollection.size());
    assertEquals("Hello", filteredCollection.get(0).getText());
  }

  @Test
  public void testFilterBy() {
    checkFilterMethod(true);
  }

  @Test
  public void testExclude() {
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
    assertEquals(1, filteredCollection.size());
    assertEquals("Hello", filteredCollection.get(0).getText());
  }

  @Test
  public void testExcludeWith() {
    checkExcludeMethod(true);
  }

  @Test
  public void testFind() {
    checkFindMethod(false);
  }

  private void checkFindMethod(boolean useBy) {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    Condition condition = Condition.text("Hello");
    SelenideElement foundElement = useBy ? collection.findBy(condition) : collection.find(condition);
    assertEquals("Hello", foundElement.getText());
  }

  @Test
  public void testFindBy() {
    checkFindMethod(true);
  }

  @Test
  public void testTexts() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    List<String> elementsTexts = collection.texts();
    assertEquals(asList("Hello", "Mark"), elementsTexts);
  }

  @Test
  public void testStaticGetTexts() {
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    String[] elementsTexts = ElementsCollection.getTexts(asList(element1, element2));
    String[] expectedTexts = new String[]{"Hello", "Mark"};
    assertEquals(expectedTexts.length, elementsTexts.length);
    for (int index = 0; index < expectedTexts.length; index++) {
      assertEquals(expectedTexts[index], elementsTexts[index]);
    }
  }

  @Test
  public void testStaticGetTextsWithWebDriverException() {
    doThrow(new WebDriverException("Failed to fetch elements")).when(element1).getText();
    when(element2.getText()).thenReturn("Mark");
    String[] elementsTexts = ElementsCollection.getTexts(asList(element1, element2));
    String[] expectedTexts = new String[]{"org.openqa.selenium.WebDriverException: Failed to fetch elements", "Mark"};
    assertEquals(expectedTexts.length, elementsTexts.length);
    for (int index = 0; index < expectedTexts.length; index++) {
      assertTrue(elementsTexts[index].contains(expectedTexts[index]));
    }
  }

  @Test
  public void testElementsToStringOnNullCollection() {
    assertEquals("[not loaded yet...]", ElementsCollection.elementsToString(null));
  }

  @Test
  public void testFirstMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    assertEquals("Hello", collection.first().getText());
  }

  @Test
  public void testFirstNElementsMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2, element3));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    when(element3.getText()).thenReturn("Twen");
    ElementsCollection firstTwoElements = collection.first(2);
    assertEquals(2, firstTwoElements.size());
    assertEquals("Hello", firstTwoElements.get(0).getText());
    assertEquals("Mark", firstTwoElements.get(1).getText());
  }

  @Test
  public void testLastMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    assertEquals("Mark", collection.last().getText());
  }

  @Test
  public void testLasttNElementsMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2, element3));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    when(element3.getText()).thenReturn("Twen");
    ElementsCollection firstTwoElements = collection.last(2);
    assertEquals(2, firstTwoElements.size());
    assertEquals("Mark", firstTwoElements.get(0).getText());
    assertEquals("Twen", firstTwoElements.get(1).getText());
  }

  @Test
  public void testIteratorMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2, element3));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    when(element3.getText()).thenReturn("Twen");
    Iterator<SelenideElement> iterator = collection.iterator();
    assertNotNull(iterator);
    assertTrue(iterator instanceof SelenideElementIterator);
    assertTrue(iterator.hasNext());
  }

  @Test
  public void testIteratorListMethod() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2, element3));
    when(element1.getText()).thenReturn("Hello");
    when(element2.getText()).thenReturn("Mark");
    when(element3.getText()).thenReturn("Twen");
    ListIterator<SelenideElement> iteratorList = collection.listIterator(1);
    assertNotNull(iteratorList);
    assertTrue(iteratorList instanceof SelenideElementListIterator);
    assertTrue(iteratorList.hasNext());
  }

  @Test
  public void doesNotWait_ifConditionAlreadyMatches() {
    WebElementsCollection source = mock(WebElementsCollection.class);
    ElementsCollection collection = spy(new ElementsCollection(source));
    when(source.getElements()).thenReturn(asList(element1, element2));

    collection.shouldHave(size(2));
    verify(collection, never()).sleep(anyLong());
  }

  @Test
  public void sleepsAsLessAsPossible_untilConditionGetsMatched() {
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
  public void toStringFetchedCollectionFromWebdriverIfNotFetchedYet() {
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    assertEquals("[\n\t<h1></h1>,\n\t<h2></h2>\n]", collection.toString());
  }

  @Test
  public void toStringPrintsErrorIfFailedToFetchElements() {
    when(source.getElements()).thenThrow(new WebDriverException("Failed to fetch elements"));
    assertEquals("[WebDriverException: Failed to fetch elements]", new ElementsCollection(source).toString());
  }

  private WebElement element(String tag) {
    WebElement element = mock(WebElement.class);
    when(element.getTagName()).thenReturn(tag);
    when(element.isDisplayed()).thenReturn(true);
    when(element.isEnabled()).thenReturn(true);
    return element;
  }
}
