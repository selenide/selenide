package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class NoneMatchTest {
  @Test
  void testToString() {
    assertThat(new NoneMatch("Is gentlemen", it -> it.getText().startsWith("Mr.")))
      .hasToString("none of elements to match [Is gentlemen] predicate");
  }
}
