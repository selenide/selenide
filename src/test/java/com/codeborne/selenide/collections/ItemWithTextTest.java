package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class ItemWithTextTest {
  @Test
  void testToString() {
    assertThat(new ItemWithText("Test-One"))
      .hasToString("Text Test-One");
  }
}
