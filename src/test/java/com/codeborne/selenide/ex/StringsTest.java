package com.codeborne.selenide.ex;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.ex.Strings.join;
import static org.assertj.core.api.Assertions.assertThat;

class StringsTest {
  @Test
  void joinTwoStrings() {
    assertThat(join("Screenshot: x", "Source: y")).isEqualTo(String.format("Screenshot: x%nSource: y"));
    assertThat(join("Screenshot: x", "")).isEqualTo("Screenshot: x");
    assertThat(join("", "Source: y")).isEqualTo("Source: y");
    assertThat(join("", "")).isEqualTo("");
  }

  @Test
  void joinMultipleStrings() {
    assertThat(join("Screenshot: x", "Source: y", "Timeout: z")).isEqualTo(String.format("Screenshot: x%nSource: y%nTimeout: z"));
    assertThat(join("Screenshot: x", "", "Timeout: z")).isEqualTo(String.format("Screenshot: x%nTimeout: z"));
    assertThat(join("Screenshot: x", "Source: y", "")).isEqualTo(String.format("Screenshot: x%nSource: y"));
    assertThat(join("", "Source: y", "Timeout: z")).isEqualTo(String.format("Source: y%nTimeout: z"));
    assertThat(join("", "", "Timeout: z")).isEqualTo("Timeout: z");
    assertThat(join("", "", "")).isEqualTo("");
  }
}
