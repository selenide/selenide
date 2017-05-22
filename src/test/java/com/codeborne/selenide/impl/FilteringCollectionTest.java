package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import org.junit.Test;
import org.mockito.Mockito;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class FilteringCollectionTest {

  @Test
  public void testConstructor() throws NoSuchFieldException, IllegalAccessException {
    WebElementsCollection mockedCollection = Mockito.mock(WebElementsCollection.class);

    FilteringCollection filteringCollection = new FilteringCollection(mockedCollection, Condition.visible);

    Field originalCollectionField = filteringCollection.getClass().getDeclaredField("originalCollection");
    originalCollectionField.setAccessible(true);

    Field filterField = filteringCollection.getClass().getDeclaredField("filter");
    filterField.setAccessible(true);

    assertEquals(mockedCollection, originalCollectionField.get(filteringCollection));
    assertEquals(Condition.visible, filterField.get(filteringCollection));
  }

  @Test
  public void testGetActualElement() {
    WebElement mockedWebElement1 = Mockito.mock(WebElement.class);
    WebElement mockedWebElement2 = Mockito.mock(WebElement.class);

    when(mockedWebElement1.isDisplayed()).thenReturn(false);
    when(mockedWebElement2.isDisplayed()).thenReturn(true);

    WebElementsCollection mockedCollection = Mockito.mock(WebElementsCollection.class);
    when(mockedCollection.getActualElements()).thenReturn(Arrays.asList(mockedWebElement1, mockedWebElement2));
    FilteringCollection filteringCollection = new FilteringCollection(mockedCollection, Condition.visible);

    List<WebElement> actualElements = filteringCollection.getActualElements();
    assertEquals(1, actualElements.size());
    assertEquals(mockedWebElement2, actualElements.get(0));
  }

  @Test
  public void testDescription() {
    WebElementsCollection mockedCollection = Mockito.mock(WebElementsCollection.class);
    when(mockedCollection.description()).thenReturn("Collection description");
    FilteringCollection filteringCollection = new FilteringCollection(mockedCollection, Condition.visible);
    assertEquals("Collection description.filter(visible)", filteringCollection.description());
  }

}
