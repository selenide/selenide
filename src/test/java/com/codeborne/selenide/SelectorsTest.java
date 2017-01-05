package com.codeborne.selenide;

import com.codeborne.selenide.Selectors.ByText;
import com.codeborne.selenide.Selectors.WithText;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SelectorsTest {
  @Test
  public void withTextUsesXPath() {
    By selector = Selectors.withText("john");
    assertTrue(selector instanceof By.ByXPath);
    assertEquals("with text: john", selector.toString());
    assertEquals(".//*/text()[contains(normalize-space(.), \"john\")]/parent::*", ((WithText) selector).getXPath());
  }

  @Test
  public void withTextEscapesQuotes() {
    By selector = Selectors.withText("Ludvig'van\"Beethoven");
    assertEquals("with text: Ludvig'van\"Beethoven", selector.toString());
    assertTrue(selector instanceof By.ByXPath);
    assertEquals(".//*/text()[contains(normalize-space(.), concat(\"Ludvig'van\", '\"', \"Beethoven\"))]/parent::*", 
        ((WithText) selector).getXPath());
  }

  @Test
  public void byTextUsesXPath() {
    By selector = Selectors.byText("john");
    assertEquals("by text: john", selector.toString());
    assertTrue(selector instanceof By.ByXPath);
    assertEquals(".//*/text()[normalize-space(.) = \"john\"]/parent::*", ((ByText) selector).getXPath());
  }

  @Test
  public void byTextEscapesQuotes() {
    By selector = Selectors.byText("Ludvig'van\"Beethoven");
    assertEquals("by text: Ludvig'van\"Beethoven", selector.toString());
    assertTrue(selector instanceof By.ByXPath);
    assertEquals(".//*/text()[normalize-space(.) = concat(\"Ludvig'van\", '\"', \"Beethoven\")]/parent::*", 
        ((ByText) selector).getXPath());
  }

  @Test
  public void byAttributeUsesXPath() {
    By selector = Selectors.byAttribute("value", "катя");
    assertEquals("By.cssSelector: [value='катя']", selector.toString());
  }

  @Test
  public void byAttributeEscapesQuotes() {
    By selector = Selectors.byAttribute("value", "Ludvig'van\"Beethoven");
    assertEquals("By.cssSelector: [value='Ludvig'van\"Beethoven']", selector.toString());
  }

  @Test
  public void userCanFindElementByAnyAttribute() {
    By selector = Selectors.by("data-account-id", "666");
    assertEquals("By.cssSelector: [data-account-id='666']", selector.toString());
  }

  @Test
  public void byTitleUsesXPath() {
    By selector = Selectors.byTitle("PDF report");
    assertEquals("By.cssSelector: [title='PDF report']", selector.toString());
  }

  @Test
  public void byValueUsesXPath() {
    By selector = Selectors.byValue("водокачка");
    assertEquals("By.cssSelector: [value='водокачка']", selector.toString());
  }
}
