package com.codeborne.selenide;

import com.codeborne.selenide.Selectors.ByText;
import com.codeborne.selenide.Selectors.WithText;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.title;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SelectorsTest {
  @Test
  public void withTextUsesXPath() {
    By selector = Selectors.withText("john");
    assertTrue(selector instanceof ExBy.ExByXPath);
    assertEquals("with text: john", selector.toString());
    assertEquals(".//*/text()[contains(normalize-space(.), \"john\")]/parent::*", ((WithText) selector).getXPath());
  }

  @Test
  public void withTextEscapesQuotes() {
    By selector = Selectors.withText("Ludvig'van\"Beethoven");
    assertEquals("with text: Ludvig'van\"Beethoven", selector.toString());
    assertTrue(selector instanceof ExBy.ExByXPath);
    assertEquals(".//*/text()[contains(normalize-space(.), concat(\"Ludvig'van\", '\"', \"Beethoven\"))]/parent::*", 
        ((WithText) selector).getXPath());
  }

  @Test
  public void byTextUsesXPath() {
    By selector = Selectors.byText("john");
    assertEquals("by text: john", selector.toString());
    assertTrue(selector instanceof ExBy.ExByXPath);
    assertEquals(".//*/text()[normalize-space(.) = \"john\"]/parent::*", ((ByText) selector).getXPath());
  }

  @Test
  public void byTextEscapesQuotes() {
    By selector = Selectors.byText("Ludvig'van\"Beethoven");
    assertEquals("by text: Ludvig'van\"Beethoven", selector.toString());
    assertTrue(selector instanceof ExBy.ExByXPath);
    assertEquals(".//*/text()[normalize-space(.) = concat(\"Ludvig'van\", '\"', \"Beethoven\")]/parent::*", 
        ((ByText) selector).getXPath());
  }

  @Test
  public void byAttributeUsesXPath() {
    By selector = Selectors.byAttribute("value", "катя");
    assertEquals("By.xpath: .//*[@value=\"катя\"]", selector.toString());
  }

  @Test
  public void byAttributeEscapesQuotes() {
    By selector = Selectors.byAttribute("value", "Ludvig'van\"Beethoven");
    assertEquals("By.xpath: .//*[@value=concat(\"Ludvig'van\", '\"', \"Beethoven\")]", selector.toString());
  }

  @Test
  public void userCanFindElementByAnyAttribute() {
    By selector = Selectors.by("data-account-id", "666");
    assertEquals("By.xpath: .//*[@data-account-id=\"666\"]", selector.toString());
  }

  @Test
  public void byTitleUsesXPath() {
    By selector = Selectors.byTitle("PDF report");
    assertEquals("By.xpath: .//*[@title=\"PDF report\"]", selector.toString());
  }

  @Test
  public void byValueUsesXPath() {
    By selector = Selectors.byValue("водокачка");
    assertEquals("By.xpath: .//*[@value=\"водокачка\"]", selector.toString());
  }

  @Test
  public void fullDescriptionIsDisplayed() {
    By selector = Selectors.byText("john").as("Some user-friendly description");
    assertTrue(selector instanceof ExBy.ExByXPath);
    assertEquals("Some user-friendly description (by text: john)", selector.toString());

    selector = Selectors.byText("john").as("");
    assertEquals("by text: john", selector.toString());

    selector = Selectors.byText("john");
    assertEquals("by text: john", selector.toString());
  }

  @Test
  public void elementDescriptionIsDisplayed() {
    try {
      $("somecss").as("Some description").click();
    }
    catch (Throwable ex) {
      if (ex instanceof ElementNotFound)
        assertThat(ex.getMessage(), containsString("Some description (somecss)"));
      else
        throw ex;
    }
  }

  @Test
  public void collectionDescriptionIsDisplayed() {
    try {
      $$("somecss").as("Some description").get(0);
    }
    catch (Throwable ex) {
      if (ex instanceof ElementNotFound)
        assertThat(ex.getMessage(), containsString("Some description (somecss)"));
      else
        throw ex;
    }
  }

}
