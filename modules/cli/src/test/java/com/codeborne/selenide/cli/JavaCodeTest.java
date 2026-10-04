package com.codeborne.selenide.cli;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.cli.JavaCode.string;
import static org.assertj.core.api.Assertions.assertThat;

class JavaCodeTest {
  @Test
  void wrapsPlainTextInQuotes() {
    assertThat(string("hello")).isEqualTo("\"hello\"");
  }

  @Test
  void escapesDoubleQuotes() {
    assertThat(string("a\"b")).isEqualTo("\"a\\\"b\"");
  }

  @Test
  void escapesBackslash() {
    assertThat(string("a\\b")).isEqualTo("\"a\\\\b\"");
  }

  @Test
  void escapesWhitespaceControlCharacters() {
    assertThat(string("a\nb\tc\rd")).isEqualTo("\"a\\nb\\tc\\rd\"");
  }

  @Test
  void handlesEmptyString() {
    assertThat(string("")).isEqualTo("\"\"");
  }
}
