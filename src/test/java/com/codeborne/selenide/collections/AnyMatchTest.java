package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class AnyMatchTest {
  @Test
  void testToString() {
    assertThat(new AnyMatch("Is a lady", it -> it.getText().startsWith("Lady")))
      .hasToString("any of elements to match [Is a lady] predicate");
  }
}
