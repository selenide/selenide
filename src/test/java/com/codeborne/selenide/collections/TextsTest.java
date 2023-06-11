package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

final class TextsTest {
  @Test
  void testToString() {
    assertThat(new Texts(asList("One", "Two")))
      .hasToString("texts [One, Two]");
  }
}
