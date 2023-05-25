package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class AnyMatchTest {
  @Test
  void testToString() {
    assertThat(new AnyMatch("Predicate description", it -> true))
      .hasToString("any match [Predicate description] predicate");
  }
}
