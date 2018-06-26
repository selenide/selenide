package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TextsTest {
  @Test
  void reduceSpacesRemovesReplacesMultipleSpacesBySingleSpace() {
    Assertions.assertEquals("Bruce Willis", Html.text.reduceSpaces("Bruce   \n\t   Willis"));
    Assertions.assertEquals("", Html.text.reduceSpaces(""));
    Assertions.assertEquals("", Html.text.reduceSpaces("   "));
    Assertions.assertEquals("a", Html.text.reduceSpaces("a"));
    Assertions.assertEquals("a", Html.text.reduceSpaces("  a\n"));
    Assertions.assertEquals("Bruce Willis", Html.text.reduceSpaces("     Bruce   \n\t   Willis  \n\n\n"));
  }
}
