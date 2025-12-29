package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.TypeOptions.text;
import static org.assertj.core.api.Assertions.assertThat;

class TypeOptionsTest {
  @Test
  void masksSensitiveValues() {
    assertThat(text("u").sensitive()).hasToString("\"*\" (delay: PT0.2S, clearFirst: true)");
    assertThat(text("us").sensitive()).hasToString("\"**\" (delay: PT0.2S, clearFirst: true)");
    assertThat(text("use").sensitive()).hasToString("\"***\" (delay: PT0.2S, clearFirst: true)");
    assertThat(text("user").sensitive()).hasToString("\"****\" (delay: PT0.2S, clearFirst: true)");
    assertThat(text("скажи друг").sensitive()).hasToString("\"**********\" (delay: PT0.2S, clearFirst: true)");
  }

  @Test
  void showsOnlyTextByDefault() {
    assertThat(text("johny")).hasToString("\"johny\" (delay: PT0.2S, clearFirst: true)");
  }

  @Test
  void showsTextAndClearFirst() {
    assertThat(text("johny").clearFirst(false)).hasToString("\"johny\" (delay: PT0.2S, clearFirst: false)");
  }

  @Test
  void showsMaskedTextAndDelay() {
    assertThat(text("johny").sensitive().withDelay(Duration.ZERO)).hasToString("\"*****\" (delay: PT0S, clearFirst: true)");
  }
}
