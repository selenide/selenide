package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.SetValueMethod.JS;
import static com.codeborne.selenide.SetValueOptions.withText;
import static org.assertj.core.api.Assertions.assertThat;

class SetValueOptionsTest {
  @Test
  void masksSensitiveValues() {
    assertThat(withText("u").sensitive()).hasToString("*");
    assertThat(withText("us").sensitive()).hasToString("**");
    assertThat(withText("use").sensitive()).hasToString("***");
    assertThat(withText("user").sensitive()).hasToString("****");
    assertThat(withText("скажи друг").sensitive()).hasToString("**********");
  }

  @Test
  void showsOnlyTextByDefault() {
    assertThat(withText("johny")).hasToString("johny");
  }

  @Test
  void showsTextAndMethod() {
    assertThat(withText("johny").usingMethod(JS)).hasToString("\"johny\" (feat. JS)");
  }

  @Test
  void showsMaskedTextAndMethod() {
    assertThat(withText("johny").sensitive().usingMethod(JS)).hasToString("\"*****\" (feat. JS)");
  }
}
