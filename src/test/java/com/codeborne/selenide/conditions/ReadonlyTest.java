package com.codeborne.selenide.conditions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReadonlyTest {
  @Test
  void toStringShouldBeConcise() {
    assertThat(new Readonly()).hasToString("readonly");
  }
}
