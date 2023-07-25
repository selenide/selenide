package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

final class TextsInAnyOrderTest {
  @Test
  void testToString() {
    assertThat(new TextsInAnyOrder(asList("One", "Two")))
      .hasToString("TextsInAnyOrder [One, Two]");
  }
}
