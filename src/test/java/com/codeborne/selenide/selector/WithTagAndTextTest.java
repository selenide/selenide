package com.codeborne.selenide.selector;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

class WithTagAndTextTest {
  @Test
  void withTextUsesXPath() {
    WithTagAndText selector = new WithTagAndText("div", "john");
    assertThat(selector)
      .isInstanceOf(By.ByXPath.class)
      .hasToString("by tag: div; with text: john");
    assertThat(selector.getXPath())
      .isEqualTo(".//div/text()[contains(normalize-space(translate(string(.), '\t\n\r ', '    ')), \"john\")]/parent::*");
  }

  @Test
  void withTextEscapesQuotes() {
    WithTagAndText selector = new WithTagAndText("table", "Ludvig'van\"Beethoven");
    assertThat(selector)
      .hasToString("by tag: table; with text: Ludvig'van\"Beethoven")
      .isInstanceOf(By.ByXPath.class);
    assertThat(selector.getXPath())
      .isEqualTo(".//table/text()[contains(normalize-space(translate(string(.), '\t\n\r ', '    ')), " +
        "concat(\"Ludvig'van\", '\"', \"Beethoven\"))]/parent::*");
  }
}
