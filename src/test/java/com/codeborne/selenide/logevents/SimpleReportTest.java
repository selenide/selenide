package com.codeborne.selenide.logevents;

import org.junit.jupiter.api.Test;

final class SimpleReportTest {
  @Test
  void reportShouldNotThrowNpe() {
    new SimpleReport().finish("test");
  }
}
