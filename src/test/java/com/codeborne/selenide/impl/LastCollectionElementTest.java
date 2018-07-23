package com.codeborne.selenide.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.StaleElementReferenceException;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LastCollectionElementTest implements WithAssertions {
  private WebElementsCollection mockedElementsCollection = mock(WebElementsCollection.class);
  private SelenideElement mockedElement1 = mock(SelenideElement.class);
  private SelenideElement mockedElement2 = mock(SelenideElement.class);

  private LastCollectionElement lastCollectionElement;

  @BeforeEach
  void setup() throws IllegalAccessException, InvocationTargetException, InstantiationException {
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
  void testGetElementMethod() {
    assertThat(lastCollectionElement.getWebElement())
      .isEqualTo(mockedElement2);
  }

  @Test
  void testGetElementMethodWhenStaleElementReferenceExceptionThrown() {
    checkGetElementsMethodWithException(new StaleElementReferenceException("Something went wrong"));
  }

  private <T extends Throwable> void checkGetElementsMethodWithException(T exception) {
    doThrow(exception).when(mockedElement2).isEnabled();
    when(mockedElementsCollection.getElements()).thenReturn(Collections.singletonList(mockedElement1));
    assertThat(lastCollectionElement.getWebElement())
      .isEqualTo(mockedElement1);
  }

  @Test
  void testGetElementMethodWhenIndexOutBoundExceptionThrown() {
    checkGetElementsMethodWithException(new IndexOutOfBoundsException());
  }

  @Test
  void testCreateElementNotFoundErrorMethodWhenCollectionIsEmpty() {
    when(mockedElementsCollection.getElements()).thenReturn(Collections.emptyList());
    ElementNotFound notFoundError = lastCollectionElement
      .createElementNotFoundError(Condition.be(Condition.empty), new StaleElementReferenceException("stale error"));
    assertThat(notFoundError)
      .hasMessage("Element not found {Collection description}\nExpected: visible");
  }

  @Test
  void testCreateElementNotFoundErrorMethodWhenCollectionIsNotEmpty() {
    ElementNotFound notFoundError = lastCollectionElement
      .createElementNotFoundError(Condition.be(Condition.empty), new StaleElementReferenceException("stale error"));
    assertThat(notFoundError)
      .hasMessage("Element not found {Collection description.last}\nExpected: be empty");
  }

  @Test
  void testToString() {
    assertThat(lastCollectionElement)
      .hasToString("Collection description.last");
  }
}
