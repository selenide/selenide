package com.codeborne.selenide.collections;

import com.codeborne.selenide.Mocks;
import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;

class SizeGreaterThanOrEqualTest implements WithAssertions {
  @Test
  void applyWithEmptyList() {
    assertThat(new SizeGreaterThanOrEqual(10).apply(emptyList()))
      .isFalse();
  }

  @Test
  void applyWithWrongSizeList() {
    assertThat(new SizeGreaterThanOrEqual(10).apply(singletonList(mock(WebElement.class))))
      .isFalse();
  }

  @Test
  void applyWithSameSize() {
    assertThat(new SizeGreaterThanOrEqual(1).apply(singletonList(mock(WebElement.class))))
      .isTrue();
  }

  @Test
  void applyWithGreaterSize() {
    assertThat(new SizeGreaterThanOrEqual(1).apply(asList(mock(WebElement.class), mock(WebElement.class))))
      .isTrue();
  }

  @Test
  void failMethod() {
    WebElementsCollection collection = Mocks.mockCollection("Collection description");

    assertThatThrownBy(() ->
      new SizeGreaterThanOrEqual(10).fail(collection,
        emptyList(),
        new Exception("Exception message"),
        10000))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch. " +
        "Expected: >= 10, actual: 0, collection: Collection description\n" +
        "Elements: []");
  }

  @Test
  void testToString() {
    assertThat(new SizeGreaterThanOrEqual(10))
      .hasToString("size >= 10");
  }
}
