package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class MethodCalledOnElementPassesOnTest extends IntegrationTest {
  @BeforeEach
  void openPage() {
    givenHtml(
      "<ul>Hello to:",
      "<li class='the-expanse detective'>Miller <label>detective</label></li>",
      "<li class='the-expanse missing'>Julie Mao</li>",
      "</ul>"
    );
    Configuration.timeout = 0;
  }

  @Test
  void shouldCondition_When$Element() {
    SelenideElement element = $("ul li");

    element.shouldHave(text("Miller"));
  }

  @Test
  void actionWithoutWaiting__When$Element() {
    SelenideElement element = $("ul li");

    assertTrue(element.isDisplayed());
  }

  @Test
  void actionWithoutWaiting_When$Element_WithNonExistentWebElement() {
    SelenideElement element = $("ul .nonexistent");

    assertFalse(element.exists());
  }

  @Test
  void shouldCondition_WhenCollectionElementByIndex() {
    SelenideElement element = $$("ul li").get(0);

    element.shouldHave(text("Miller"));
  }

  @Test
  void actionWithoutWaiting_Exists_WhenCollectionElement_WithNonExistentWebElement() {
    SelenideElement element = $$("ul li").findBy(cssClass("nonexistent"));

    assertFalse(element.exists());
  }

  @Test
  void actionWithoutWaiting_Exists_WhenCollectionElement_WithExistentWebElement() {
    SelenideElement element = $$("ul li").findBy(cssClass("detective"));

    assertTrue(element.exists());
  }

  @Test
  void actionWithoutWaiting_IsDisplayed_WhenCollectionElement_WithNonExistentWebElement() {
    SelenideElement element = $$("ul li").findBy(cssClass("nonexistent"));

    assertFalse(element.isDisplayed());
  }

  @Test
  void actionWithoutWaiting_IsDisplayed_WhenCollectionElement_WithExistentWebElement() {
    SelenideElement element = $$("ul li").findBy(cssClass("detective"));

    assertTrue(element.isDisplayed());
  }

  @Test
  void actionWithoutWaiting_Is_WhenCollectionElement_WithNonExistentWebElement() {
    SelenideElement element = $$("ul li").findBy(cssClass("nonexistent"));

    assertFalse(element.is(visible));
  }

  @Test
  void actionWithoutWaiting_Is_WhenCollectionElement_WithExistentWebElement() {
    SelenideElement element = $$("ul li").findBy(cssClass("detective"));

    assertTrue(element.is(visible));
  }

  @Test
  void actionWithoutWaiting_ToString_WhenCollectionElement_WithNonExistentWebElement() {
    SelenideElement element = $$("ul li").findBy(cssClass("nonexistent"));

    assertThat(element.toString())
      .startsWith("Element not found {ul li.findBy(css class 'nonexistent')}");
  }

  @Test
  void actionWithoutWaiting_ToString_WhenCollectionElement_WithExistentWebElement() {
    SelenideElement element = $$("ul li").findBy(cssClass("detective"));

    assertTrue(element.toString().contains("Miller"));
  }

  @Test
  void actionWithVisibilityWaiting_WhenCollectionElementByIndex() {
    SelenideElement element = $$("ul li").get(0);

    element.click();
  }

  @Test
  void shouldCondition_WhenCollectionElementByCondition() {
    SelenideElement element = $$("li").findBy(cssClass("the-expanse"));

    element.shouldBe(visible);
  }

  @Test
  void actionWithExistenceWaiting_WhenCollectionElementByCondition() {
    SelenideElement element = $$("li").findBy(cssClass("the-expanse"));

    assertEquals("Miller detective", element.text());
  }

  @Test
  void shouldCondition_WhenInnerElement() {
    SelenideElement element = $("ul").find(".the-expanse");

    element.shouldBe(visible);
  }

  @Test
  void actionWithVisibilityWaiting_WhenInnerElement() {
    SelenideElement element = $("ul").find(".the-expanse");

    element.doubleClick();
  }

  /******************************************************
   * More complicated useful options
   * $$.filterBy(condition).findBy(condition).find
   ******************************************************/

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementByConditionInFilteredCollection() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).findBy(cssClass("detective")).find("label");

    element.shouldHave(exactText("detective"));
  }

  /******************************************************
   * More complicated useful options
   * $$.filterBy(condition).get(index).find
   ******************************************************/
  @Test
  void shouldCondition_WhenInnerElementFromOuterElementFoundByIndexInFilteredCollection() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).get(0).find("label");

    element.shouldHave(exactText("detective"));
  }
}
