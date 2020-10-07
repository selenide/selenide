package com.codeborne.selenide.impl;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

final class TextsTest implements WithAssertions {
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
