package com.codeborne.selenide.mcp;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SnapshotBuilderTest {
  @Test
  void loadsSnapshotJsFromClasspath() {
    SnapshotBuilder builder = new SnapshotBuilder();
    assertThat(builder).isNotNull();
  }
}
