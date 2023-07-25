package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class SizeGreaterThanOrEqualTest {
  @Test
  void testToString() {
    assertThat(new SizeGreaterThanOrEqual(10))
      .hasToString("size >= 10");
  }
}
