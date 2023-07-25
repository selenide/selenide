package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class AllMatchTest {
  @Test
  void testToString() {
    assertThat(new AllMatch("Peaceful", it -> it.getText().contains("Peace")))
      .hasToString("all elements to match [Peaceful] predicate");
  }
}
