package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.ClickMethod.DEFAULT;
import static com.codeborne.selenide.ClickMethod.JS;
import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.Keys.ALT;
import static org.openqa.selenium.Keys.CONTROL;
import static org.openqa.selenium.Keys.LEFT_ALT;
import static org.openqa.selenium.Keys.LEFT_CONTROL;
import static org.openqa.selenium.Keys.LEFT_SHIFT;
import static org.openqa.selenium.Keys.SHIFT;

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
      .hasToString("method: JS, offsetX: 100, offsetY: 500, force: false, keys: []");

    assertThat(usingJavaScript().offsetX(100))
      .hasToString("method: JS, offsetX: 100, offsetY: 0, force: false, keys: []");

    assertThat(usingJavaScript().offsetY(500))
      .hasToString("method: JS, offsetX: 0, offsetY: 500, force: false, keys: []");

    assertThat(usingDefaultMethod().offsetX(100).offsetY(500))
      .hasToString("method: DEFAULT, offsetX: 100, offsetY: 500, force: false, keys: []");

    assertThat(usingDefaultMethod().holdingKeys(ALT, SHIFT, CONTROL))
      .hasToString("method: DEFAULT, offsetX: 0, offsetY: 0, force: false, keys: [alt, shift, control]");

    assertThat(usingDefaultMethod().holdingKeys(LEFT_ALT, LEFT_SHIFT, LEFT_CONTROL))
      .hasToString("method: DEFAULT, offsetX: 0, offsetY: 0, force: false, keys: [left alt, left shift, left control]");
  }
}
