package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static org.assertj.core.api.Assertions.assertThat;

final class ClickOptionsTest {

  @Test
  void printsOptionsToTestReport() {
    assertThat(usingDefaultMethod())
      .hasToString("method: DEFAULT");

    assertThat(usingJavaScript())
      .hasToString("method: JS");

    assertThat(usingDefaultMethod(0, 0))
      .hasToString("method: DEFAULT");

    assertThat(usingJavaScript(0, 0))
      .hasToString("method: JS");

    assertThat(usingDefaultMethod(100, 500))
      .hasToString("method: DEFAULT, offsetX: 100, offsetY: 500");

    assertThat(usingJavaScript(500, 100))
      .hasToString("method: JS, offsetX: 500, offsetY: 100");
  }
}
