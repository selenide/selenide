package com.codeborne.selenide.appium.selector;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.appium.AppiumSelectors.withText;
import static org.assertj.core.api.Assertions.assertThat;

class WithTextTest {
  @Test
  void testToString() {
    assertThat(withText("Hello")).hasToString("[text*=Hello]");
  }
}
