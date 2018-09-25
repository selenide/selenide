package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelenideElementListIteratorTest implements WithAssertions {
  private Driver driver = mock(Driver.class);
  private WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);

  @BeforeEach
  void setUp() {
    when(mockedWebElementCollection.driver()).thenReturn(driver);
  }

  @Test
  void testHasPrevious() {
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 1);
    assertThat(selenideElementIterator.hasPrevious())
      .isTrue();
  }

  @Test
  void testPrevious() {
    WebElement mockedWebElement = mock(WebElement.class);
    when(mockedWebElement.isDisplayed()).thenReturn(true);
    when(mockedWebElement.getTagName()).thenReturn("a");
    when(mockedWebElement.getText()).thenReturn("selenide");

    when(mockedWebElementCollection.getElements()).thenReturn(singletonList(mockedWebElement));

    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 1);
    SelenideElement previous = selenideElementIterator.previous();
    assertThat(previous)
      .isNotNull();
    assertThat(previous)
      .hasToString("<a>selenide</a>");
  }

  @Test
  void testNextIndex() {
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 1);
    assertThat(selenideElementIterator.nextIndex())
      .isEqualTo(2);
  }

  @Test
  void testPreviousIndex() {
    SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 3);
    assertThat(selenideElementIterator.previousIndex())
      .isEqualTo(2);
  }

  @Test
  void testAdd() {
    try {
      SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 0);
      selenideElementIterator.add(mock(SelenideElement.class));
    } catch (UnsupportedOperationException e) {
      assertThat(e)
        .hasMessage("Cannot add elements to web page");
    }
  }

  @Test
  void testSet() {
    try {
      SelenideElementListIterator selenideElementIterator = new SelenideElementListIterator(mockedWebElementCollection, 0);
      selenideElementIterator.set(mock(SelenideElement.class));
    } catch (UnsupportedOperationException e) {
      assertThat(e)
        .hasMessage("Cannot set elements to web page");
    }
  }
}
