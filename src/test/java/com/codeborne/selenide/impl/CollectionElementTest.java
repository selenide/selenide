package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Mocks.mockCollection;
import static com.codeborne.selenide.Mocks.mockWebElement;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class CollectionElementTest {
  private final Driver driver = new DriverStub();

  @Test
  void wrap() {
    WebElement mockedWebElement = mockWebElement("a", "hello");
    CollectionSource collection = new WebElementsCollectionWrapper(driver, singletonList(mockedWebElement));
    SelenideElement selenideElement = CollectionElement.wrap(collection, 0);
    assertThat(selenideElement)
      .hasToString("$$(1 elements)[0]");
  }

  @Test
  void getWebElement() {
    WebElement mockedWebElement1 = mock();
    WebElement mockedWebElement2 = mock();
    CollectionSource collection = mockCollection("", mockedWebElement1, mockedWebElement2);
    CollectionElement collectionElement = new CollectionElement(collection, 1);

    assertThat(collectionElement.getWebElement()).isEqualTo(mockedWebElement2);
  }

  @Test
  void getSearchCriteria() {
    CollectionSource collection = mock();
    when(collection.getSearchCriteria()).thenReturn("#report .line");
    CollectionElement collectionElement = new CollectionElement(collection, 2);
    assertThat(collectionElement.getSearchCriteria()).isEqualTo("#report .line[2]");
  }

  @Test
  void testToString() {
    CollectionSource collection = mock();
    when(collection.shortDescription()).thenReturn("#report .line");
    CollectionElement collectionElement = new CollectionElement(collection, 1);
    assertThat(collectionElement).hasToString("#report .line[1]");
  }

  @Test
  void createElementNotFoundErrorWithEmptyCollection() {
    CollectionSource collection = mock();
    when(collection.driver()).thenReturn(driver);
    when(collection.getSearchCriteria()).thenReturn("#report .line");
    CollectionElement collectionElement = new CollectionElement(collection, 33);

    WebElementCondition mockedCollection = mock();
    ElementNotFound elementNotFoundError = collectionElement.createElementNotFoundError(mockedCollection, new Error("Error message"));

    assertThat(elementNotFoundError)
      .hasMessage(String.format("Element not found {#report .line[33]}%n" +
        "Expected: visible%n" +
        "Timeout: 0 ms.%n" +
        "Caused by: java.lang.Error: Error message"));
  }

  @Test
  void createElementNotFoundErrorWithNonEmptyCollection() {
    CollectionSource collection = mock();
    when(collection.driver()).thenReturn(driver);
    when(collection.getSearchCriteria()).thenReturn("#report .line");
    when(collection.getElements()).thenReturn(singletonList(mock()));
    CollectionElement collectionElement = new CollectionElement(collection, 1);

    WebElementCondition mockedCollection = mock();
    when(mockedCollection.toString()).thenReturn("Reason description");
    ElementNotFound elementNotFoundError = collectionElement.createElementNotFoundError(mockedCollection, new Error("Error message"));

    assertThat(elementNotFoundError)
      .hasMessage(String.format("Element not found {#report .line[1]}%n" +
        "Expected: Reason description%n" +
        "Timeout: 0 ms.%n" +
        "Caused by: java.lang.Error: Error message"));
  }
}
