package com.codeborne.selenide;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.TextMatchOptions.fullText;
import static com.codeborne.selenide.TextMatchOptions.partialText;
import static org.assertj.core.api.Assertions.assertThat;

class TextMatchOptionsTest {
  @Test
  void toStringFormat() {
    assertThat(fullText()).hasToString("full text, case sensitive");
    assertThat(fullText().caseInsensitive()).hasToString("full text, case insensitive");
    assertThat(partialText()).hasToString("partial text, case sensitive");
    assertThat(partialText().caseInsensitive()).hasToString("partial text, case insensitive");
    assertThat(partialText().ignoreWhitespaces()).hasToString("partial text, case sensitive, ignore whitespaces");
    assertThat(partialText().preserveWhitespaces()).hasToString("partial text, case sensitive, preserve whitespaces");
  }
}
