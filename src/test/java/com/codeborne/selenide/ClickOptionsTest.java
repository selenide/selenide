package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.ClickMethod.DEFAULT;
import static com.codeborne.selenide.ClickMethod.JS;
import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static org.assertj.core.api.Assertions.assertThat;

final class ClickOptionsTest {
  @Test
  void javaScriptMethod() {
    assertThat(usingJavaScript().clickMethod()).isEqualTo(JS);
  }

  @Test
  void defaultMethod() {
    assertThat(usingDefaultMethod().clickMethod()).isEqualTo(DEFAULT);
  }

  @Test
  void defaultUnchangedOffsets() {
    ClickOptions clickOptions = usingDefaultMethod();
    assertThat(clickOptions.offsetX()).isEqualTo(0);
    assertThat(clickOptions.offsetY()).isEqualTo(0);
  }

  @Test
  void customOffsets() {
    ClickOptions clickOptions = usingDefaultMethod().offset(1, 2);
    assertThat(clickOptions.offsetX()).isEqualTo(1);
    assertThat(clickOptions.offsetY()).isEqualTo(2);
  }

  @Test
  void customOffsetXAndDefaultOffsetY() {
    ClickOptions clickOptions = usingDefaultMethod().offsetX(1);
    assertThat(clickOptions.offsetX()).isEqualTo(1);
    assertThat(clickOptions.offsetY()).isEqualTo(0);
  }

  @Test
  void printsOptionsToTestReport() {
    assertThat(usingDefaultMethod())
      .hasToString("method: DEFAULT");

    assertThat(usingJavaScript())
      .hasToString("method: JS");

    assertThat(usingJavaScript().offset(100, 500))
      .hasToString("method: JS, offsetX: 100, offsetY: 500");

    assertThat(usingJavaScript().offsetX(100))
      .hasToString("method: JS, offsetX: 100, offsetY: 0");

    assertThat(usingJavaScript().offsetY(500))
      .hasToString("method: JS, offsetX: 0, offsetY: 500");

    assertThat(usingDefaultMethod().offsetX(100).offsetY(500))
      .hasToString("method: DEFAULT, offsetX: 100, offsetY: 500");
  }
}
