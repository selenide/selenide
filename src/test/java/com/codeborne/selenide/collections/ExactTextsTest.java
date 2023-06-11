package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class ExactTextsTest {

  @Test
  void testToString() {
    ExactTexts exactTexts = new ExactTexts("One", "Two", "Three");
    assertThat(exactTexts)
      .hasToString("Exact texts [One, Two, Three]");
  }
}
