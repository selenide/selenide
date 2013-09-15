package com.codeborne.selenide.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TextsTest {
  @Test
  public void reduceSpacesRemovesReplacesMultipleSpacesBySingleSpace() {
    assertEquals("Bruce Willis", Html.text.reduceSpaces("Bruce   \n\t   Willis"));
    assertEquals("", Html.text.reduceSpaces(""));
    assertEquals("", Html.text.reduceSpaces("   "));
    assertEquals("a", Html.text.reduceSpaces("a"));
    assertEquals("a", Html.text.reduceSpaces("  a\n"));
    assertEquals("Bruce Willis", Html.text.reduceSpaces("     Bruce   \n\t   Willis  \n\n\n"));
  }
}
