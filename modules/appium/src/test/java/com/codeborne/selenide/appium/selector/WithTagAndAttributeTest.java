package com.codeborne.selenide.appium.selector;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.appium.AppiumSelectors.withTagAndAttribute;
import static org.assertj.core.api.Assertions.assertThat;

class WithTagAndAttributeTest {
  @Test
  void testToString() {
    assertThat(withTagAndAttribute("div", "accessibility-id", "hello"))
      .hasToString("div[accessibility-id*=hello]");
  }
}
