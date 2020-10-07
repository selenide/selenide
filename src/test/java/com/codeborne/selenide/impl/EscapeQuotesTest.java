package com.codeborne.selenide.impl;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.support.ui.Quotes;

final class EscapeQuotesTest implements WithAssertions {
  @Test
  void textWithoutQuotes() {
    assertThat(Quotes.escape("john"))
      .isEqualTo("\"john\"");
  }

  @Test
  void textWithApostrophe() {
    assertThat(Quotes.escape("John Mc'Clain"))
      .isEqualTo("\"John Mc'Clain\"");
  }

  @Test
  void textWithQuote() {
    assertThat(Quotes.escape("Cafe \"Rock Cafe\""))
      .isEqualTo("'Cafe \"Rock Cafe\"'");
  }

  @Test
  void textWithQuoteAndApostrophe() {
    assertThat(Quotes.escape("A'la cafe \"Rock Cafe\""))
      .isEqualTo("concat(\"A'la cafe \", '\"', \"Rock Cafe\", '\"')");
  }

  @Test
  void textWithApostropheAndQuote() {
    assertThat(Quotes.escape("Cafe \"Rock Cafe\" isn't cool?"))
      .isEqualTo("concat(\"Cafe \", '\"', \"Rock Cafe\", '\"', \" isn't cool?\")");
  }

  @Test
  void textWithApostropheInsideQuotes() {
    assertThat(Quotes.escape("Cafe \"Rock'n'Roll\""))
      .isEqualTo("concat(\"Cafe \", '\"', \"Rock'n'Roll\", '\"')");
  }

  @Test
  void textWithQuotesInsideApostrophe() {
    assertThat(Quotes.escape("The 'I am not \"Oracle\"' approach"))
      .isEqualTo("concat(\"The 'I am not \", '\"', \"Oracle\", '\"', \"' approach\")");
  }
}
