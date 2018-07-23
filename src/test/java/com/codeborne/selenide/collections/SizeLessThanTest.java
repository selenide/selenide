package com.codeborne.selenide.collections;

import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SizeLessThanTest implements WithAssertions {
  @Test
  void testApplyWithWrongSizeList() {
    assertThat(new SizeLessThan(1).apply(asList(mock(WebElement.class), mock(WebElement.class))))
      .isFalse();
  }

  @Test
  void testApplyWithCorrectSizeLessThan() {
    assertThat(new SizeLessThan(2).apply(singletonList(mock(WebElement.class))))
      .isTrue();
  }

  @Test
  void testFailMethod() {
    WebElementsCollection mockedWebElementCollection = mock(WebElementsCollection.class);
    when(mockedWebElementCollection.description()).thenReturn("Collection description");

    try {
      new SizeLessThan(10).fail(mockedWebElementCollection,
        emptyList(),
        new Exception("Exception message"),
        10000);
    } catch (ListSizeMismatch ex) {
      assertThat(ex)
        .hasMessage(": expected: < 10, actual: 0, collection: Collection description\nElements: []");
    }
  }

  @Test
  void testToString() {
    assertThat(new SizeLessThan(10))
      .hasToString("size < 10");
  }
}
