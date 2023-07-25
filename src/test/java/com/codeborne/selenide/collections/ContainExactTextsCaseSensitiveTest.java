package com.codeborne.selenide.collections;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ContainExactTextsCaseSensitiveTest {
  @Test
  void testToString() {
    ContainExactTextsCaseSensitive expectedTexts =
      new ContainExactTextsCaseSensitive("Test-One", "Test-Two", "Test-Three");

    assertThat(expectedTexts)
      .hasToString("Contains exact texts case-sensitive [Test-One, Test-Two, Test-Three]");
  }
}
