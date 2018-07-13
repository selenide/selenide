package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import org.junit.Test;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilteringCollectionTest {
  @Test
  public void testGetActualElement() {
    WebElement mockedWebElement1 = mock(WebElement.class);
    WebElement mockedWebElement2 = mock(WebElement.class);

    when(mockedWebElement1.isDisplayed()).thenReturn(false);
    when(mockedWebElement2.isDisplayed()).thenReturn(true);

    WebElementsCollection mockedCollection = mock(WebElementsCollection.class);
    when(mockedCollection.getElements()).thenReturn(asList(mockedWebElement1, mockedWebElement2));
    FilteringCollection filteringCollection = new FilteringCollection(mockedCollection, Condition.visible);

    List<WebElement> actualElements = filteringCollection.getElements();
    assertEquals(1, actualElements.size());
    assertEquals(mockedWebElement2, actualElements.get(0));
  }

  @Test
  public void testDescription() {
    WebElementsCollection mockedCollection = mock(WebElementsCollection.class);
    when(mockedCollection.description()).thenReturn("Collection description");
    FilteringCollection filteringCollection = new FilteringCollection(mockedCollection, Condition.visible);
    assertEquals("Collection description.filter(visible)", filteringCollection.description());
  }

}
