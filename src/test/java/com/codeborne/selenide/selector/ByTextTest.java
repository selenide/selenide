package com.codeborne.selenide.selector;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

class ByTextTest {
  @Test
  void byTextUsesXPath() {
    ByText selector = new ByText("john");
    assertThat(selector)
      .hasToString("by text: john");
    assertThat(selector)
      .isInstanceOf(By.ByXPath.class);
    assertThat(selector.getXPath())
      .isEqualTo(".//*/text()[normalize-space(translate(string(.), '\t\n\r\u00a0', '    ')) = \"john\"]/parent::*");
  }

  @Test
  void byTextEscapesQuotes() {
    ByText selector = new ByText("Ludvig'van\"Beethoven");
    assertThat(selector)
      .hasToString("by text: Ludvig'van\"Beethoven");
    assertThat(selector)
      .isInstanceOf(By.ByXPath.class);
    assertThat(selector.getXPath())
      .isEqualTo(".//*/text()[normalize-space(translate(string(.), '\t\n\r ', '    ')) = " +
        "concat(\"Ludvig'van\", '\"', \"Beethoven\")]/parent::*");
  }
}
