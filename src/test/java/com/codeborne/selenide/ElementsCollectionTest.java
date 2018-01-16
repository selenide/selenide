package com.codeborne.selenide;

import com.codeborne.selenide.impl.WebElementsCollection;
import com.codeborne.selenide.rules.MockWebdriverContainer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Configuration.browser;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ElementsCollectionTest {
  @Rule
  public MockWebdriverContainer mockWebdriverContainer = new MockWebdriverContainer();
  
  WebElement element1 = element("h1");
  WebElement element2 = element("h2");

  @Before
  public final void mockWebDriver() {
    browser = null;
  }

  @Test
  public void doesNotWait_ifConditionAlreadyMatches() {
    WebElementsCollection source = mock(WebElementsCollection.class);
    ElementsCollection collection = spy(new ElementsCollection(source));
    when(source.getActualElements()).thenReturn(asList(element1, element2));

    collection.shouldHave(size(2));
    
    verify(collection, never()).sleep(anyLong());
  }

  @Test
  public void sleepsAsLessAsPossible_untilConditionGetsMatched() {
    WebElementsCollection source = mock(WebElementsCollection.class);
    ElementsCollection collection = spy(new ElementsCollection(source));
    when(source.getActualElements()).thenReturn(
        asList(element1),
        asList(element1, element2),
        asList(element1, element2, element2)
    );

    collection.shouldHave(size(3));
    
    verify(collection, times(2)).sleep(anyLong());
  }

  @Test
  public void toStringFetchedCollectionFromWebdriverIfNotFetchedYet() {
    WebElementsCollection source = mock(WebElementsCollection.class);
    ElementsCollection collection = new ElementsCollection(source);
    when(source.getElements()).thenReturn(asList(element1, element2));
    assertEquals("[\n\t<h1></h1>,\n\t<h2></h2>\n]", collection.toString());
  }

  @Test
  public void toStringPrintsErrorIfFailedToFetchElements() {
    WebElementsCollection source = mock(WebElementsCollection.class);
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
