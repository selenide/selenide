package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.TextsMismatch;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CollectionMethods {
  @Before
  public void openTestPageWithJQuery() {
    open(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));
  }

  @Test
  public void useTwoDollarsToGetListOfElements() {
    $$("#radioButtons input").shouldHave(size(4));
    getElements(By.cssSelector("#radioButtons input")).shouldHave(size(4));

    $("#radioButtons").$$("input").shouldHave(size(4));
    $("#radioButtons").$$(By.tagName("input")).shouldHave(size(4));
    $("#radioButtons").findAll("input").shouldHave(size(4));
    $("#radioButtons").findAll(By.tagName("input")).shouldHave(size(4));
  }

  @Test
  public void canUseSizeMethod() {
    assertEquals(1, $$(By.name("domain")).size());
    assertEquals(1, $$("#theHiddenElement").size());
    assertEquals(4, $$("#radioButtons input").size());
    assertEquals(4, $$(By.xpath("//select[@name='domain']/option")).size());
    assertEquals(0, $$(By.name("non-existing-element")).size());
  }

  @Test
  public void canCheckIfCollectionIsEmpty() {
    $$(By.name("#dynamic-content-container span")).shouldBe(empty);
    $$(By.name("non-existing-element")).shouldBe(empty);
    $$(byText("Loading...")).shouldBe(empty);
  }

  @Test
  public void canCheckSizeOfCollection() {
    $$(By.name("domain")).shouldHaveSize(1);
    $$("#theHiddenElement").shouldHaveSize(1);
    $$("#radioButtons input").shouldHaveSize(4);
    $$(By.xpath("//select[@name='domain']/option")).shouldHaveSize(4);
    $$(By.name("non-existing-element")).shouldHaveSize(0);
    $$("#dynamic-content-container span").shouldHave(size(2));
  }

  @Test
  public void shouldWaitUntilCollectionGetsExpectedSize() {
    ElementsCollection spans = $$("#dynamic-content-container span");

    spans.shouldHave(size(2)); // appears after 2 seconds

    assertEquals(2, spans.size());
    assertArrayEquals(new String[]{"dynamic content", "dynamic content2"}, spans.getTexts());
  }

  @Test
  public void canCheckThatElementsHaveCorrectTexts() {
    $$("#dynamic-content-container span").shouldHave(
        texts("dynamic content", "dynamic content2"));
  }

  @Test(expected = ElementNotFound.class)
  public void textsCheckThrowsElementNotFound() {
    $$(".non-existing-elements").shouldHave(texts("content1", "content2"));
  }

  @Test(expected = TextsMismatch.class)
  public void textsCheckThrowsTextsMismatch() {
    $$("#dynamic-content-container span").shouldHave(texts("static-content1", "static-content2", "static3"));
  }

  @Test
  public void userCanFilterOutMatchingElements() {
    $$("#multirowTable tr").shouldHaveSize(2);
    $$("#multirowTable tr").filterBy(text("Norris")).shouldHaveSize(1);
    $$("#multirowTable tr").filterBy(cssClass("inexisting")).shouldHaveSize(0);
  }

  @Test
  public void userCanExcludeMatchingElements() {
    $$("#multirowTable tr").shouldHaveSize(2);
    $$("#multirowTable tr").excludeWith(text("Chack")).shouldHaveSize(0);
    $$("#multirowTable tr").excludeWith(cssClass("inexisting")).shouldHaveSize(2);
  }

  @Test
  public void userCanFindMatchingElementFromList() {
    $$("#multirowTable tr").findBy(text("Norris")).shouldHave(text("Norris"));
  }
}
