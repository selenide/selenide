package com.codeborne.selenide.appium.selector;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.appium.AppiumSelectors.withTagAndText;
import static org.assertj.core.api.Assertions.assertThat;

class WithTagAndTextTest {
  @Test
  void testToString() {
    assertThat(withTagAndText("div", "Hello"))
      .hasToString("div[text*=Hello]");
  }
}
