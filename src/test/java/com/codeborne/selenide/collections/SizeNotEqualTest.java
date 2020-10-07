package com.codeborne.selenide.collections;

import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.impl.WebElementsCollection;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Mocks.mockCollection;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;

final class SizeNotEqualTest implements WithAssertions {
  @Test
  void applyWithEmptyList() {
    assertThat(new SizeNotEqual(10).test(emptyList()))
      .isTrue();
  }

  @Test
  void applyWithWrongSizeList() {
    assertThat(new SizeNotEqual(10).test(singletonList(mock(WebElement.class))))
      .isTrue();
  }

  @Test
  void applyWithCorrectSizeNotEqual() {
    assertThat(new SizeNotEqual(1).test(singletonList(mock(WebElement.class))))
      .isFalse();
  }

  @Test
  void failMethod() {
    WebElementsCollection collection = mockCollection("Collection description");

    assertThatThrownBy(() -> new SizeNotEqual(10).fail(collection,
      emptyList(),
      new Exception("Exception message"),
      10000))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith(
        String.format("List size mismatch: expected: <> 10, actual: 0, collection: Collection description%nElements: []"));
  }

  @Test
  void testToString() {
    assertThat(new SizeNotEqual(10))
      .hasToString("size <> 10");
  }
}
