package com.codeborne.selenide.appium.selector;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.appium.AppiumSelectors.byTagAndText;
import static org.assertj.core.api.Assertions.assertThat;

class ByTagAndTextTest {
  @Test
  void testToString() {
    assertThat(byTagAndText("div", "Hello"))
      .hasToString("div[text=Hello]");
  }
}
