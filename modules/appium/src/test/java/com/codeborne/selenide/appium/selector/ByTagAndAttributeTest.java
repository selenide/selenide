package com.codeborne.selenide.appium.selector;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.appium.AppiumSelectors.byTagAndAttribute;
import static org.assertj.core.api.Assertions.assertThat;

class ByTagAndAttributeTest {
  @Test
  void testToString() {
    assertThat(byTagAndAttribute("div", "accessibility-id", "hello"))
      .hasToString("div[accessibility-id=hello]");
  }
}
