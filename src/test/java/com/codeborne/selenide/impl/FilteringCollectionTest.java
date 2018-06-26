package com.codeborne.selenide.impl;

import java.util.List;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilteringCollectionTest {
  @Test
  void testGetActualElement() {
    WebElement mockedWebElement1 = mock(WebElement.class);
    WebElement mockedWebElement2 = mock(WebElement.class);

    when(mockedWebElement1.isDisplayed()).thenReturn(false);
    when(mockedWebElement2.isDisplayed()).thenReturn(true);

    WebElementsCollection mockedCollection = mock(WebElementsCollection.class);
    when(mockedCollection.getActualElements()).thenReturn(asList(mockedWebElement1, mockedWebElement2));
    FilteringCollection filteringCollection = new FilteringCollection(mockedCollection, Condition.visible);

    List<WebElement> actualElements = filteringCollection.getActualElements();
    Assertions.assertEquals(1, actualElements.size());
    Assertions.assertEquals(mockedWebElement2, actualElements.get(0));
  }

  @Test
  void testDescription() {
    WebElementsCollection mockedCollection = mock(WebElementsCollection.class);
    when(mockedCollection.description()).thenReturn("Collection description");
    FilteringCollection filteringCollection = new FilteringCollection(mockedCollection, Condition.visible);
    Assertions.assertEquals("Collection description.filter(visible)", filteringCollection.description());
  }
}
