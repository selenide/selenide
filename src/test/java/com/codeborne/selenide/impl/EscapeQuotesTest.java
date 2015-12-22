package com.codeborne.selenide.impl;

import org.junit.Test;
import org.openqa.selenium.support.ui.Quotes;

import static org.junit.Assert.assertEquals;

public class EscapeQuotesTest {
  @Test
  public void textWithoutQuotes() {
    assertEquals("\"john\"", Quotes.escape("john"));
  }

  @Test
  public void textWithApostrophe() {
    assertEquals("\"John Mc'Clain\"", Quotes.escape("John Mc'Clain"));
  }

  @Test
  public void textWithQuote() {
    assertEquals("'Cafe \"Rock Cafe\"'", Quotes.escape("Cafe \"Rock Cafe\""));
  }

  @Test
  public void textWithQuoteAndApostrophe() {
    assertEquals("concat(\"A'la cafe \", '\"', \"Rock Cafe\", '\"')",
        Quotes.escape("A'la cafe \"Rock Cafe\""));
  }

  @Test
  public void textWithApostropheAndQuote() {
    assertEquals("concat(\"Cafe \", '\"', \"Rock Cafe\", '\"', \" isn't cool?\")",
        Quotes.escape("Cafe \"Rock Cafe\" isn't cool?"));
  }

  @Test
  public void textWithApostropheInsideQuotes() {
    assertEquals("concat(\"Cafe \", '\"', \"Rock'n'Roll\", '\"')",
        Quotes.escape("Cafe \"Rock'n'Roll\""));
  }

  @Test
  public void textWithQuotesInsideApostrophe() {
    assertEquals("concat(\"The 'I am not \", '\"', \"Oracle\", '\"', \"' approach\")",
        Quotes.escape("The 'I am not \"Oracle\"' approach"));
  }
}
