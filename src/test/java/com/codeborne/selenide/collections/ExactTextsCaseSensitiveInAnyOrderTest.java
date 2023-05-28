package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class ExactTextsCaseSensitiveInAnyOrderTest {
  @Test
  void shouldHaveCorrectToString() {
    assertThat(new ExactTextsCaseSensitiveInAnyOrder("One", "Two"))
      .hasToString("Exact texts case sensitive in any order [One, Two]");
  }
}
