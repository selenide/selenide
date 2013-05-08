package com.codeborne.selenide.integrationtests;

import com.codeborne.selenide.ElementsCollection;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CollectionMethods {
  @Before
  public void openTestPageWithJQuery() {
    open(currentThread().getContextClassLoader().getResource("page_with_selects_without_jquery.html"));
  }

  @Test
  public void canUseSizeMethod() {
    assertEquals(1, $$(By.name("domain")).size());
    assertEquals(1, $$("#theHiddenElement").size());
    assertEquals(4, $$("#radioButtons input").size());
    assertEquals(4, $$(By.xpath("//select[@name='domain']/option")).size());
    assertEquals(0, $$(By.name("non-existing-element")).size());
    assertEquals(0, $$("#dynamic-content-container span").size());
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
    $$("#dynamic-content-container span").shouldHaveSize(0);
  }

  @Test
  public void shouldWaitUntilCollectionGetsExpectedSize() {
    ElementsCollection spans = $$("#dynamic-content-container span");

    assertEquals(0, spans.size());
    assertArrayEquals(new String[]{}, spans.getTexts());

    spans.shouldHave(size(1)); // appears after 1 second

    assertEquals(1, spans.size());
    assertArrayEquals(new String[]{"dynamic content"}, spans.getTexts());

    spans.shouldHave(size(2)); // appears after 2 seconds

    assertEquals(2, spans.size());
    assertArrayEquals(new String[]{"dynamic content", "dynamic content2"}, spans.getTexts());
  }

  @Test
  public void canCheckThatElementsHaveCorrectTexts() {
    $$("#dynamic-content-container span").shouldHave(texts("dynamic-content", "dynamic-content2"));
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
