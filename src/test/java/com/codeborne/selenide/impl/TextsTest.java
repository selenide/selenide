package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

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

  @ParameterizedTest
  @CsvFileSource(resources = "/unicodeSpaces.csv")
  void unicodeSpacesReduceTest(String unicodeSpace) {
    String unicodeSpaceString = org.apache.commons.text.StringEscapeUtils.unescapeJava("\\%s".formatted(unicodeSpace));
    assertThat(Html.text.reduceSpaces("This is nonbreakable " + unicodeSpaceString + "space"))
      .isEqualTo("This is nonbreakable space");
  }

}
