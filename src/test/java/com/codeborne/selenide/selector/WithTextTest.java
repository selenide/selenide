package com.codeborne.selenide.selector;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

class WithTextTest {
  @Test
  void withTextUsesXPath() {
    WithText selector = new WithText("john");
    assertThat(selector)
      .isInstanceOf(By.ByXPath.class);
    assertThat(selector)
      .hasToString("with text: john");
    assertThat(selector.getXPath())
      .isEqualTo(".//*/text()[contains(normalize-space(translate(string(.), '\t\n\r ', '    ')), \"john\")]/parent::*");
  }

  @Test
  void withTextEscapesQuotes() {
    WithText selector = new WithText("Ludvig'van\"Beethoven");
    assertThat(selector)
      .hasToString("with text: Ludvig'van\"Beethoven");
    assertThat(selector)
      .isInstanceOf(By.ByXPath.class);
    assertThat(selector.getXPath())
      .isEqualTo(".//*/text()[contains(normalize-space(translate(string(.), '\t\n\r ', '    ')), " +
        "concat(\"Ludvig'van\", '\"', \"Beethoven\"))]/parent::*");
  }
}
