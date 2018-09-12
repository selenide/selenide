package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.SelenideElement;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelenideElementIteratorTest implements WithAssertions {
  private Driver driver = mock(Driver.class);
  private WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);

  @BeforeEach
  void setUp() {
    when(mockedWebElementCollection.driver()).thenReturn(driver);
  }

  @Test
  void testHasNext() {
    when(mockedWebElementCollection.getElements()).thenReturn(singletonList(mock(WebElement.class)));
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
    assertThat(selenideElementIterator.hasNext())
      .isTrue();
  }

  @Test
  void testDoesNotHasNext() {
    when(mockedWebElementCollection.getElements()).thenReturn(emptyList());
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
    assertThat(selenideElementIterator.hasNext())
      .isFalse();
  }

  @Test
  void testNext() {
    WebElement mockedWebElement = mock(WebElement.class);
    when(mockedWebElement.isDisplayed()).thenReturn(true);
    when(mockedWebElement.getTagName()).thenReturn("a");
    when(mockedWebElement.getText()).thenReturn("selenide");

    when(mockedWebElementCollection.driver()).thenReturn(driver);
    when(mockedWebElementCollection.getElements()).thenReturn(singletonList(mockedWebElement));
    SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
    SelenideElement nextElement = selenideElementIterator.next();

    assertThat(nextElement)
      .isNotNull();
    assertThat(nextElement)
      .hasToString("<a>selenide</a>");
    assertThat(selenideElementIterator.hasNext())
      .isFalse();
  }

  @Test
  void testRemove() {
    try {
      SelenideElementIterator selenideElementIterator = new SelenideElementIterator(mockedWebElementCollection);
      selenideElementIterator.remove();
    } catch (UnsupportedOperationException e) {
      assertThat(e)
        .hasMessage("Cannot remove elements from web page");
    }
  }
}
