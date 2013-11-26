package com.codeborne.selenide.impl;

import org.junit.Test;

import static com.codeborne.selenide.impl.Quotes.escape;
import static org.junit.Assert.assertEquals;

public class EscapeQuotesTest {
  @Test
  public void textWithoutQuotes() {
    assertEquals("\"john\"", escape.quotes("john"));
  }

  @Test
  public void textWithApostrophe() {
    assertEquals("\"John Mc'Clain\"", escape.quotes("John Mc'Clain"));
  }

  @Test
  public void textWithQuote() {
    assertEquals("'Cafe \"Rock Cafe\"'", escape.quotes("Cafe \"Rock Cafe\""));
  }

  @Test
  public void textWithQuoteAndApostrophe() {
    assertEquals("concat(\"A'la cafe \", '\"', \"Rock Cafe\", '\"')",
        escape.quotes("A'la cafe \"Rock Cafe\""));
  }

  @Test
  public void textWithApostropheAndQuote() {
    assertEquals("concat(\"Cafe \", '\"', \"Rock Cafe\", '\"', \" isn't cool?\")",
        escape.quotes("Cafe \"Rock Cafe\" isn't cool?"));
  }

  @Test
  public void textWithApostropheInsideQuotes() {
    assertEquals("concat(\"Cafe \", '\"', \"Rock'n'Roll\", '\"')",
        escape.quotes("Cafe \"Rock'n'Roll\""));
  }

  @Test
  public void textWithQuotesInsideApostrophe() {
    assertEquals("concat(\"The 'I am not \", '\"', \"Oracle\", '\"', \"' approach\")",
        escape.quotes("The 'I am not \"Oracle\"' approach"));
  }
}
