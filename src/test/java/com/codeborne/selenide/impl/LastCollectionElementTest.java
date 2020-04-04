package com.codeborne.selenide.impl;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.StaleElementReferenceException;

import java.util.Collections;

import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockElement;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class LastCollectionElementTest implements WithAssertions {
  private SelenideElement element1 = mockElement("Hello");
  private SelenideElement element2 = mockElement("World");
  private WebElementsCollection collection = mockCollection("Collection description", element1, element2);
  private LastCollectionElement lastCollectionElement = new LastCollectionElement(collection);

  @Test
  void getElementMethod() {
    assertThat(lastCollectionElement.getWebElement()).isEqualTo(element2);
  }

  @Test
  void getElementMethodWhenStaleElementReferenceExceptionThrown() {
    checkGetElementsMethodWithException(new StaleElementReferenceException("Something went wrong"));
  }

  private <T extends Throwable> void checkGetElementsMethodWithException(T exception) {
    doThrow(exception).when(element2).isEnabled();
    when(collection.getElements()).thenReturn(Collections.singletonList(element1));

    assertThat(lastCollectionElement.getWebElement()).isEqualTo(element1);
  }

  @Test
  void getElementMethodWhenIndexOutBoundExceptionThrown() {
    checkGetElementsMethodWithException(new IndexOutOfBoundsException());
  }

  @Test
  void createElementNotFoundErrorMethodWhenCollectionIsEmpty() {
    when(collection.getElements()).thenReturn(Collections.emptyList());
    ElementNotFound notFoundError = lastCollectionElement
      .createElementNotFoundError(Condition.be(Condition.empty), new StaleElementReferenceException("stale error"));
    assertThat(notFoundError)
      .hasMessageStartingWith(String.format("Element not found {Collection description}%nExpected: visible"));
  }

  @Test
  void createElementNotFoundErrorMethodWhenCollectionIsNotEmpty() {
    ElementNotFound notFoundError = lastCollectionElement
      .createElementNotFoundError(Condition.be(Condition.empty), new StaleElementReferenceException("stale error"));
    assertThat(notFoundError)
      .hasMessageStartingWith(String.format("Element not found {Collection description.last}%nExpected: be empty"));
  }

  @Test
  void testToString() {
    assertThat(lastCollectionElement)
      .hasToString("Collection description.last");
  }
}
