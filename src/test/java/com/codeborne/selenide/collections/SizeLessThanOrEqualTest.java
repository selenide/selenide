package com.codeborne.selenide.collections;

import com.codeborne.selenide.ex.ListSizeMismatch;
import com.codeborne.selenide.impl.CollectionSource;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Mocks.mockCollection;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

final class SizeLessThanOrEqualTest {
  @Test
  void applyWithWrongSizeList() {
    assertThat(new SizeLessThanOrEqual(1).test(asList(mock(), mock())))
      .isFalse();
  }

  @Test
  void applyWithSameSize() {
    assertThat(new SizeLessThanOrEqual(1).test(singletonList(mock())))
      .isTrue();
  }

  @Test
  void applyWithLessSize() {
    assertThat(new SizeLessThanOrEqual(2).test(singletonList(mock())))
      .isTrue();
  }

  @Test
  void failMethod() {
    CollectionSource collection = mockCollection("Collection description");

    assertThatThrownBy(() ->
      new SizeLessThanOrEqual(10).fail(collection,
        emptyList(),
        new Exception("Exception message"),
        10000))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith(
        String.format("List size mismatch: expected: <= 10, actual: 0, collection: Collection description%nElements: []"));
  }

  @Test
  void testToString() {
    assertThat(new SizeLessThanOrEqual(10))
      .hasToString("size <= 10");
  }
}
