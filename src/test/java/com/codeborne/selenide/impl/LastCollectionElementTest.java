package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;

import org.openqa.selenium.StaleElementReferenceException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LastCollectionElementTest {
  private WebElementsCollection mockedElementsCollection = mock(WebElementsCollection.class);
  private SelenideElement mockedElement1 = mock(SelenideElement.class);
  private SelenideElement mockedElement2 = mock(SelenideElement.class);

  private LastCollectionElement lastCollectionElement;

  @BeforeMethod
  public void setup() throws IllegalAccessException, InvocationTargetException, InstantiationException {
    String element1Text = "Hello";
    when(mockedElement1.getText()).thenReturn(element1Text);
    String element2Text = "World";
    when(mockedElement2.getText()).thenReturn(element2Text);
    when(mockedElementsCollection.getElements()).thenReturn(asList(mockedElement1, mockedElement2));
    String collectionDescription = "Collection description";
    when(mockedElementsCollection.description()).thenReturn(collectionDescription);

    Constructor<?>[] declaredConstructors = LastCollectionElement.class.getDeclaredConstructors();
    declaredConstructors[0].setAccessible(true);
    lastCollectionElement = (LastCollectionElement) declaredConstructors[0].newInstance(mockedElementsCollection);
  }

  @Test
  public void testGetElementMethod() {
    assertEquals(mockedElement2, lastCollectionElement.getWebElement());
  }

  @Test
  public void testGetElementMethodWhenStaleElementReferenceExceptionThrown() {
    checkGetElementsMethodWithException(new StaleElementReferenceException("Something went wrong"));
  }

  @Test
  public void testGetElementMethodWhenIndexOutBoundExceptionThrown() {
    checkGetElementsMethodWithException(new IndexOutOfBoundsException());
  }

  private <T extends Throwable> void checkGetElementsMethodWithException(T exception) {
    doThrow(exception).when(mockedElement2).isEnabled();
    when(mockedElementsCollection.getElements()).thenReturn(Collections.singletonList(mockedElement1));
    assertEquals(mockedElement1, lastCollectionElement.getWebElement());
  }

  @Test
  public void testCreateElementNotFoundErrorMethodWhenCollectionIsEmpty() {
    when(mockedElementsCollection.getElements()).thenReturn(Collections.emptyList());
    ElementNotFound notFoundError = lastCollectionElement
        .createElementNotFoundError(Condition.be(Condition.empty), new StaleElementReferenceException("stale error"));
    assertEquals("Element not found {Collection description}\n" +
                   "Expected: visible", notFoundError.getMessage());
  }

  @Test
  public void testCreateElementNotFoundErrorMethodWhenCollectionIsNotEmpty() {
    ElementNotFound notFoundError = lastCollectionElement
        .createElementNotFoundError(Condition.be(Condition.empty), new StaleElementReferenceException("stale error"));
    assertEquals("Element not found {Collection description.last}\n" +
                   "Expected: be empty", notFoundError.getMessage());
  }

  @Test
  public void testToString() {
    assertEquals("Collection description.last", lastCollectionElement.toString());
  }
}
