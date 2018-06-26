package com.codeborne.selenide.impl;

import java.util.List;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BySelectorCollectionTest {
  private SelenideElement mockedWebElement = mock(SelenideElement.class);

  @Test
  void testNoParentConstructor() {
    BySelectorCollection bySelectorCollection = new BySelectorCollection(By.id("selenide"));
    String description = bySelectorCollection.description();
    Assertions.assertEquals("By.id: selenide", description);
  }

  @Test
  void testWithWebElementParentConstructor() {
    when(mockedWebElement.getSearchCriteria()).thenReturn("By.tagName: a");

    BySelectorCollection bySelectorCollection = new BySelectorCollection(mockedWebElement, By.name("selenide"));
    String description = bySelectorCollection.description();
    Assertions.assertEquals("By.tagName: a/By.name: selenide", description);
  }

  @Test
  void testWithNotWebElementParentConstructor() {
    BySelectorCollection bySelectorCollection = new BySelectorCollection(new NotWebElement(), By.name("selenide"));
    String description = bySelectorCollection.description();
    Assertions.assertEquals("By.name: selenide", description);
  }

  @Test
  void testGetElementsMethod() {
    BySelectorCollection bySelectorCollection = spy(new BySelectorCollection(new NotWebElement(), By.name("selenide")));
    Assertions.assertEquals(mockedWebElement, bySelectorCollection.getElements().get(0));
    Assertions.assertEquals(mockedWebElement, bySelectorCollection.getElements().get(0));
    verify(bySelectorCollection, times(1)).getActualElements();
  }

  private class NotWebElement implements SearchContext {
    @Override
    public List<WebElement> findElements(By by) {
      return singletonList(mockedWebElement);
    }

    @Override
    public WebElement findElement(By by) {
      return mockedWebElement;
    }
  }
}
