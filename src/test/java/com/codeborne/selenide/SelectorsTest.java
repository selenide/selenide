package com.codeborne.selenide;

import com.codeborne.selenide.Selectors.ByText;
import com.codeborne.selenide.Selectors.WithText;
import com.codeborne.selenide.selector.ByShadow;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

final class SelectorsTest implements WithAssertions {
  @Test
  void withTextUsesXPath() {
    By selector = Selectors.withText("john");
    assertThat(selector)
      .isInstanceOf(By.ByXPath.class);
    assertThat(selector)
      .hasToString("with text: john");
    assertThat(((WithText) selector).getXPath())
      .isEqualTo(".//*/text()[contains(normalize-space(translate(string(.), '\t\n\r ', '    ')), \"john\")]/parent::*");
  }

  @Test
  void withTextEscapesQuotes() {
    By selector = Selectors.withText("Ludvig'van\"Beethoven");
    assertThat(selector)
      .hasToString("with text: Ludvig'van\"Beethoven");
    assertThat(selector)
      .isInstanceOf(By.ByXPath.class);
    assertThat(((WithText) selector).getXPath())
      .isEqualTo(".//*/text()[contains(normalize-space(translate(string(.), '\t\n\r ', '    ')), " +
        "concat(\"Ludvig'van\", '\"', \"Beethoven\"))]/parent::*");
  }

  @Test
  void byTextUsesXPath() {
    By selector = Selectors.byText("john");
    assertThat(selector)
      .hasToString("by text: john");
    assertThat(selector)
      .isInstanceOf(By.ByXPath.class);
    assertThat(((ByText) selector).getXPath())
      .isEqualTo(".//*/text()[normalize-space(translate(string(.), '\t\n\r\u00a0', '    ')) = \"john\"]/parent::*");
  }

  @Test
  void byTextEscapesQuotes() {
    By selector = Selectors.byText("Ludvig'van\"Beethoven");
    assertThat(selector)
      .hasToString("by text: Ludvig'van\"Beethoven");
    assertThat(selector)
      .isInstanceOf(By.ByXPath.class);
    assertThat(((ByText) selector).getXPath())
      .isEqualTo(".//*/text()[normalize-space(translate(string(.), '\t\n\r ', '    ')) = " +
        "concat(\"Ludvig'van\", '\"', \"Beethoven\")]/parent::*");
  }

  @Test
  void byAttributeUsesXPath() {
    By selector = Selectors.byAttribute("value", "катя");
    assertThat(selector)
      .hasToString("By.cssSelector: [value=\"катя\"]");
  }

  @Test
  void byAttributeEscapesQuotes() {
    assertThat(Selectors.byAttribute("value", "Ludvig'van\"Beethoven"))
      .hasToString("By.cssSelector: [value=\"Ludvig'van\\\"Beethoven\"]");

    assertThat(Selectors.byAttribute("value", "i love back\\\\slashes"))
      .hasToString("By.cssSelector: [value=\"i love back\\\\\\\\slashes\"]");

    assertThat(Selectors.byAttribute("value", "denzel \\\"equalizer\\\" washington"))
      .hasToString("By.cssSelector: [value=\"denzel \\\\\\\"equalizer\\\\\\\" washington\"]");
  }

  @Test
  void userCanFindElementByAnyAttribute() {
    By selector = Selectors.by("data-account-id", "666");
    assertThat(selector)
      .hasToString("By.cssSelector: [data-account-id=\"666\"]");
  }

  @Test
  void byTitleUsesXPath() {
    By selector = Selectors.byTitle("PDF report");
    assertThat(selector)
      .hasToString("By.cssSelector: [title=\"PDF report\"]");
  }

  @Test
  void byValueUsesXPath() {
    By selector = Selectors.byValue("водокачка");
    assertThat(selector)
      .hasToString("By.cssSelector: [value=\"водокачка\"]");
  }

  @Test
  void byName() {
    String name = "selenide";
    By nameSelector = Selectors.byName(name);

    assertThat(nameSelector)
      .isInstanceOf(By.ByName.class);
    assertThat(nameSelector)
      .hasToString("By.name: " + name);
  }

  @Test
  void byXpath() {
    String xpath = "html/body/div[2]/section/div[2]/div/ul/li[2]/a/span/strong/h4";
    By nameSelector = Selectors.byXpath(xpath);
    assertThat(nameSelector)
      .isInstanceOf(By.ByXPath.class);
    assertThat(nameSelector)
      .hasToString("By.xpath: " + xpath);
  }

  @Test
  void byLinkText() {
    String linkText = "click me";
    By linkTextSelector = Selectors.byLinkText(linkText);
    assertThat(linkTextSelector)
      .isInstanceOf(By.ByLinkText.class);
    assertThat(linkTextSelector)
      .hasToString("By.linkText: " + linkText);
  }

  @Test
  void byPartialLinkText() {
    String partialLinkText = "click me";
    By linkPartialTextSelector = Selectors.byPartialLinkText(partialLinkText);
    assertThat(linkPartialTextSelector)
      .isInstanceOf(By.ByPartialLinkText.class);
    assertThat(linkPartialTextSelector)
      .hasToString("By.partialLinkText: " + partialLinkText);
  }

  @Test
  void byId() {
    String id = "clickMe";
    By idSelector = Selectors.byId(id);
    assertThat(idSelector)
      .isInstanceOf(By.ById.class);
    assertThat(idSelector)
      .hasToString("By.id: " + id);
  }

  @Test
  void byCssSelector() {
    String css = ".ql>h3";
    By cssSelector = Selectors.byCssSelector(css);
    assertThat(cssSelector)
      .isInstanceOf(By.ByCssSelector.class);
    assertThat(cssSelector)
      .hasToString("By.cssSelector: " + css);
  }

  @Test
  void byClassName() {
    String className = "selenide";
    By classNameSelector = Selectors.byClassName(className);
    assertThat(classNameSelector)
      .isInstanceOf(By.ByClassName.class);
    assertThat(classNameSelector)
      .hasToString("By.className: " + className);
  }

  @Test
  void byShadowCss() {
    String target = "#target";
    String shadow = "#shadow";
    String innerShadow = "#inner-shadow";
    By cssSelector = Selectors.shadowCss(target, shadow, innerShadow);
    assertThat(cssSelector)
      .isInstanceOf(ByShadow.ByShadowCss.class);
    assertThat(cssSelector)
      .hasToString("By.cssSelector: " + shadow + " [" + innerShadow + "] " + target);
  }

  @Test
  void byTagName() {
    String tagName = "selenide";
    By tagNameSelector = Selectors.byTagName(tagName);
    assertThat(tagNameSelector)
      .isInstanceOf(By.ByTagName.class);
    assertThat(tagNameSelector)
      .hasToString("By.tagName: " + tagName);
  }
}
