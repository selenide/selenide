package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class ListSizeTest {
  @Test
  void testToString() {
    assertThat(new ListSize(10))
      .hasToString("size(10)");
  }
}
