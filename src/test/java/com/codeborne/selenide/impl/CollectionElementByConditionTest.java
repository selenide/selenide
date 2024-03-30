package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.DriverStub;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Mocks.mockWebElement;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

final class CollectionElementByConditionTest {
  private final Driver driver = new DriverStub();
  private final CollectionSource collection = mock();

  @Test
  void wrap() {
    WebElement mockedWebElement = mockWebElement("a", "hello");

    SelenideElement selenideElement = CollectionElementByCondition.wrap(
      new WebElementsCollectionWrapper(driver, singletonList(mockedWebElement)), visible);
    assertThat(selenideElement)
      .hasToString("$$(1 elements).findBy(visible)");
  }

  @Test
  void getWebElement() {
    WebElement mockedWebElement1 = mock();
    WebElement mockedWebElement2 = mock();

    List<WebElement> listOfMockedElements = asList(mockedWebElement1, mockedWebElement2);
    when(collection.getElements()).thenReturn(listOfMockedElements);
    when(mockedWebElement2.isDisplayed()).thenReturn(true);
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(collection, visible);

    assertThat(collectionElement.getWebElement())
      .isEqualTo(mockedWebElement2);
  }

  @Test
  void getSearchCriteria() {
    when(collection.shortDescription()).thenReturn("ul#employees li.employee");
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(collection, visible);
    assertThat(collectionElement)
      .hasToString(String.format("%s.findBy(visible)", "ul#employees li.employee"));
  }

  @Test
  void testToString() {
    when(collection.shortDescription()).thenReturn("ul#employees li.employee");
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(collection, visible);
    assertThat(collectionElement)
      .hasToString(String.format("%s.findBy(visible)", "ul#employees li.employee"));
  }

  @Test
  void createElementNotFoundErrorWithEmptyCollection() {
    when(collection.driver()).thenReturn(driver);
    when(collection.getSearchCriteria()).thenReturn("ul#employees li.employee");
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(collection, visible);

    ElementNotFound elementNotFoundError = collectionElement.createElementNotFoundError(visible,
      new NoSuchElementException("with class: employee"));

    assertThat(elementNotFoundError)
      .hasMessage(String.format("Element not found {ul#employees li.employee.findBy(visible)}%n" +
        "Expected: visible%n" +
        "Timeout: 0 ms.%n" +
        "Caused by: NoSuchElementException: with class: employee"));
  }

  @Test
  void createElementNotFoundErrorWithNonEmptyCollection() {
    when(collection.driver()).thenReturn(driver);
    when(collection.getSearchCriteria()).thenReturn("ul#employees li.employee");
    when(collection.getElements()).thenReturn(singletonList(mock()));
    CollectionElementByCondition collectionElement = new CollectionElementByCondition(collection, visible);

    WebElementCondition condition = text("Hello").because("Successfully logged in");
    ElementNotFound elementNotFoundError = collectionElement.createElementNotFoundError(condition, new Error("Error message"));

    assertThat(elementNotFoundError)
      .hasMessage(String.format("Element not found {ul#employees li.employee.findBy(visible)}%n" +
                                "Expected: text \"Hello\" (because Successfully logged in)%n" +
                                "Timeout: 0 ms.%n" +
                                "Caused by: java.lang.Error: Error message"
      ));
  }
}
