package com.codeborne.selenide.impl;

import com.codeborne.selenide.SelenideElement;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BySelectorCollectionTest {
  private SelenideElement mockedWebElement = mock(SelenideElement.class);

  @Test
  public void testNoParentConstructor() {
    BySelectorCollection bySelectorCollection = new BySelectorCollection(By.id("selenide"));
    String description = bySelectorCollection.description();
    assertEquals("By.id: selenide", description);
  }

  @Test
  public void testWithWebElementParentConstructor() {
    when(mockedWebElement.getSearchCriteria()).thenReturn("By.tagName: a");

    BySelectorCollection bySelectorCollection = new BySelectorCollection(mockedWebElement, By.name("selenide"));
    String description = bySelectorCollection.description();
    assertEquals("By.tagName: a/By.name: selenide", description);
  }

  @Test
  public void testWithNotWebElementParentConstructor() {
    BySelectorCollection bySelectorCollection = new BySelectorCollection(new NotWebElement(), By.name("selenide"));
    String description = bySelectorCollection.description();
    assertEquals("By.name: selenide", description);
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
