package com.codeborne.selenide;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

final class ClickOptionsTest implements WithAssertions {
  @Test
  void usingJavaScript() {
    ClickOptions clickOptions = ClickOptions
      .usingJavaScript();
    assertThat(clickOptions.clickOption())
      .isEqualTo(ClickMethod.JS);
  }

  @Test
  void usingDefaultMethod() {
    ClickOptions clickOptions = ClickOptions
      .usingDefaultMethod();
    assertThat(clickOptions.clickOption())
      .isEqualTo(ClickMethod.DEFAULT);
  }

  @Test
  void testDefaultUnchangedOffsets() {
    ClickOptions clickOptions = ClickOptions
      .usingDefaultMethod();
    assertThat(clickOptions.offsetX()).isEqualTo(0);
    assertThat(clickOptions.offsetY()).isEqualTo(0);
  }

  @Test
  void testCustomOffsets() {
    ClickOptions clickOptions = ClickOptions
      .usingDefaultMethod()
      .offset(1, 2);
    assertThat(clickOptions.offsetX()).isEqualTo(1);
    assertThat(clickOptions.offsetY()).isEqualTo(2);
  }

  @Test
  void testCustomOffsetXAndDefaultOffsetY() {
    ClickOptions clickOptions = ClickOptions
      .usingDefaultMethod()
      .offsetX(1);
    assertThat(clickOptions.offsetX()).isEqualTo(1);
    assertThat(clickOptions.offsetY()).isEqualTo(0);
  }
}
