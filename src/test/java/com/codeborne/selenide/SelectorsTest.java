package com.codeborne.selenide;

import com.codeborne.selenide.selector.ByShadowCss;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

final class SelectorsTest {

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
    By classNameSelector = Selectors.byClassName("btn-active");
    assertThat(classNameSelector).isInstanceOf(By.ByClassName.class);
    assertThat(classNameSelector).hasToString("By.className: btn-active");
  }

  @Test
  void byShadowCss() {
    By cssSelector = Selectors.shadowCss("#target", "#shadow", "#inner-shadow");
    assertThat(cssSelector)
      .isInstanceOf(ByShadowCss.class);
    assertThat(cssSelector)
      .hasToString("By.cssSelector: #shadow -> #inner-shadow -> #target");
  }

  @Test
  void byTagName() {
    By tagNameSelector = Selectors.byTagName("div");
    assertThat(tagNameSelector).isInstanceOf(By.ByTagName.class);
    assertThat(tagNameSelector).hasToString("By.tagName: div");
  }
}
