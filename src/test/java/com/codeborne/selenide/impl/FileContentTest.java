package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileContentTest {
  @Test
  void canReadFileContent() {
    assertThat(new FileContent("find-in-shadow-roots.js").content()).contains("function findInShadows");
  }
}
