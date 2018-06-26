package com.codeborne.selenide.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.Quotes;

class EscapeQuotesTest {
  @Test
  void textWithoutQuotes() {
    Assertions.assertEquals("\"john\"", Quotes.escape("john"));
  }

  @Test
  void textWithApostrophe() {
    Assertions.assertEquals("\"John Mc'Clain\"", Quotes.escape("John Mc'Clain"));
  }

  @Test
  void textWithQuote() {
    Assertions.assertEquals("'Cafe \"Rock Cafe\"'", Quotes.escape("Cafe \"Rock Cafe\""));
  }

  @Test
  void textWithQuoteAndApostrophe() {
    Assertions.assertEquals("concat(\"A'la cafe \", '\"', \"Rock Cafe\", '\"')",
      Quotes.escape("A'la cafe \"Rock Cafe\""));
  }

  @Test
  void textWithApostropheAndQuote() {
    Assertions.assertEquals("concat(\"Cafe \", '\"', \"Rock Cafe\", '\"', \" isn't cool?\")",
      Quotes.escape("Cafe \"Rock Cafe\" isn't cool?"));
  }

  @Test
  void textWithApostropheInsideQuotes() {
    Assertions.assertEquals("concat(\"Cafe \", '\"', \"Rock'n'Roll\", '\"')",
      Quotes.escape("Cafe \"Rock'n'Roll\""));
  }

  @Test
  void textWithQuotesInsideApostrophe() {
    Assertions.assertEquals("concat(\"The 'I am not \", '\"', \"Oracle\", '\"', \"' approach\")",
      Quotes.escape("The 'I am not \"Oracle\"' approach"));
  }
}
