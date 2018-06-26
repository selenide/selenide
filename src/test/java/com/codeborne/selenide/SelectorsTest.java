package com.codeborne.selenide;

import com.codeborne.selenide.Selectors.ByText;
import com.codeborne.selenide.Selectors.WithText;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectorsTest {
  @Test
  void withTextUsesXPath() {
    By selector = Selectors.withText("john");
    assertTrue(selector instanceof By.ByXPath);
    assertEquals("with text: john", selector.toString());
    assertEquals(".//*/text()[contains(normalize-space(translate(string(.), '\t\n\r\u00a0', '    ')), " +
      "\"john\")]/parent::*", ((WithText) selector).getXPath());
  }

  @Test
  void withTextEscapesQuotes() {
    By selector = Selectors.withText("Ludvig'van\"Beethoven");
    assertEquals("with text: Ludvig'van\"Beethoven", selector.toString());
    assertTrue(selector instanceof By.ByXPath);
    assertEquals(".//*/text()[contains(normalize-space(translate(string(.), '\t\n\r\u00a0', '    ')), " +
        "concat(\"Ludvig'van\", '\"', \"Beethoven\"))]/parent::*",
      ((WithText) selector).getXPath());
  }

  @Test
  void byTextUsesXPath() {
    By selector = Selectors.byText("john");
    assertEquals("by text: john", selector.toString());
    assertTrue(selector instanceof By.ByXPath);
    assertEquals(".//*/text()[normalize-space(translate(string(.), '\t\n\r\u00a0', '    ')) = \"john\"]/parent::*",
      ((ByText) selector).getXPath());
  }

  @Test
  void byTextEscapesQuotes() {
    By selector = Selectors.byText("Ludvig'van\"Beethoven");
    assertEquals("by text: Ludvig'van\"Beethoven", selector.toString());
    assertTrue(selector instanceof By.ByXPath);
    assertEquals(".//*/text()[normalize-space(translate(string(.), '\t\n\r\u00a0', '    ')) = " +
        "concat(\"Ludvig'van\", '\"', \"Beethoven\")]/parent::*",
      ((ByText) selector).getXPath());
  }

  @Test
  void byAttributeUsesXPath() {
    By selector = Selectors.byAttribute("value", "катя");
    assertEquals("By.cssSelector: [value='катя']", selector.toString());
  }

  @Test
  void byAttributeEscapesQuotes() {
    By selector = Selectors.byAttribute("value", "Ludvig'van\"Beethoven");
    assertEquals("By.cssSelector: [value='Ludvig'van\"Beethoven']", selector.toString());
  }

  @Test
  void userCanFindElementByAnyAttribute() {
    By selector = Selectors.by("data-account-id", "666");
    assertEquals("By.cssSelector: [data-account-id='666']", selector.toString());
  }

  @Test
  void byTitleUsesXPath() {
    By selector = Selectors.byTitle("PDF report");
    assertEquals("By.cssSelector: [title='PDF report']", selector.toString());
  }

  @Test
  void byValueUsesXPath() {
    By selector = Selectors.byValue("водокачка");
    assertEquals("By.cssSelector: [value='водокачка']", selector.toString());
  }

  @Test
  void byName() {
    String name = "selenide";
    By nameSelector = Selectors.byName(name);
    assertThat(nameSelector, instanceOf(By.ByName.class));
    assertEquals("By.name: " + name, nameSelector.toString());
  }

  @Test
  void byXpath() {
    String xpath = "html/body/div[2]/section/div[2]/div/ul/li[2]/a/span/strong/h4";
    By nameSelector = Selectors.byXpath(xpath);
    assertThat(nameSelector, instanceOf(By.ByXPath.class));
    assertEquals("By.xpath: " + xpath, nameSelector.toString());
  }

  @Test
  void byLinkText() {
    String linkText = "click me";
    By linkTextSelector = Selectors.byLinkText(linkText);
    assertThat(linkTextSelector, instanceOf(By.ByLinkText.class));
    assertEquals("By.linkText: " + linkText, linkTextSelector.toString());
  }

  @Test
  void byPartialLinkText() {
    String partialLinkText = "click me";
    By linkPartialTextSelector = Selectors.byPartialLinkText(partialLinkText);
    assertThat(linkPartialTextSelector, instanceOf(By.ByPartialLinkText.class));
    assertEquals("By.partialLinkText: " + partialLinkText, linkPartialTextSelector.toString());
  }

  @Test
  void byId() {
    String id = "clickMe";
    By idSelector = Selectors.byId(id);
    assertThat(idSelector, instanceOf(By.ById.class));
    assertEquals("By.id: " + id, idSelector.toString());
  }

  @Test
  void byCssSelector() {
    String css = ".ql>h3";
    By cssSelector = Selectors.byCssSelector(css);
    assertThat(cssSelector, instanceOf(By.ByCssSelector.class));
    assertEquals("By.cssSelector: " + css, cssSelector.toString());
  }

  @Test
  void byClassName() {
    String className = "selenide";
    By classNameSelector = Selectors.byClassName(className);
    assertThat(classNameSelector, instanceOf(By.ByClassName.class));
    assertEquals("By.className: " + className, classNameSelector.toString());
  }
}
