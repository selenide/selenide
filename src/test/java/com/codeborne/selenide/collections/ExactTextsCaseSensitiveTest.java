package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class ExactTextsCaseSensitiveTest {
  @Test
  void testToString() {
    ExactTextsCaseSensitive exactTextsCaseSensitive = new ExactTextsCaseSensitive("One", "Two", "Three");
    assertThat(exactTextsCaseSensitive)
      .hasToString("Exact texts case sensitive [One, Two, Three]");
  }
}
