package com.codeborne.selenide.impl;

import com.codeborne.selenide.UnitTest;
import org.junit.jupiter.api.Test;

class TextsTest extends UnitTest {
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
