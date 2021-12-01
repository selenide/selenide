package com.codeborne.selenide.selector;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

class ByTagAndTextTest {
  @Test
  void byTagAndTextUsesXPath() {
    ByTagAndText selector = new ByTagAndText("div", "john");
    assertThat(selector)
      .hasToString("by tag: div; by text: john")
      .isInstanceOf(By.ByXPath.class);
    assertThat(selector.getXPath())
      .isEqualTo(".//div/text()[normalize-space(translate(string(.), '\t\n\r\u00a0', '    ')) = \"john\"]/parent::*");
  }

  @Test
  void byTagAndTextEscapesQuotes() {
    ByTagAndText selector = new ByTagAndText("table", "Ludvig'van\"Beethoven");
    assertThat(selector)
      .hasToString("by tag: table; by text: Ludvig'van\"Beethoven")
      .isInstanceOf(By.ByXPath.class);
    assertThat(selector.getXPath())
      .isEqualTo(".//table/text()[normalize-space(translate(string(.), '\t\n\r ', '    ')) = " +
        "concat(\"Ludvig'van\", '\"', \"Beethoven\")]/parent::*");
  }
}
