package com.codeborne.selenide.logevents;

import org.junit.jupiter.api.Test;

class SimpleReportTest {
  @Test
  void reportShouldNotThrowNpe() {
    new SimpleReport().finish("test");
  }
}
