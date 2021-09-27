package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

final class TextsTest {
  @Test
  void reduceSpacesRemovesReplacesMultipleSpacesBySingleSpace() {
    assertThat(Html.text.reduceSpaces("Bruce   \n\t   Willis"))
      .isEqualTo("Bruce Willis");
    assertThat(Html.text.reduceSpaces(""))
      .isEqualTo("");
    assertThat(Html.text.reduceSpaces("   "))
      .isEqualTo("");
    assertThat(Html.text.reduceSpaces("a"))
      .isEqualTo("a");
    assertThat(Html.text.reduceSpaces("  a\n"))
      .isEqualTo("a");
    assertThat(Html.text.reduceSpaces("     Bruce   \n\t   Willis  \n\n\n"))
      .isEqualTo("Bruce Willis");
  }
}
