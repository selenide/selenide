package com.codeborne.selenide.appium.selector;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.appium.AppiumSelectors.byText;
import static org.assertj.core.api.Assertions.assertThat;

class ByTextTest {
  @Test
  void testToString() {
    assertThat(byText("Hello")).hasToString("[text=Hello]");
  }
}
