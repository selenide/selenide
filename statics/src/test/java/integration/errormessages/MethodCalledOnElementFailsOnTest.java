package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static integration.errormessages.Helper.screenshot;
import static integration.errormessages.Helper.webElementNotFound;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class MethodCalledOnElementFailsOnTest extends IntegrationTest {
  @BeforeEach
  void openPage() {
    givenHtml(
      "<ul>Hello to:",
      "<li class='the-expanse detective'>Miller <label>detective</label></li>",
      "<li class='the-expanse missing'>Julie Mao</li>",
      "</ul>"
    );
    Configuration.timeout = 3;
  }

  @Test
  void shouldCondition_When$Element_WithNonExistentElement() {
    SelenideElement element = $("ul .nonexistent");

    assertThatThrownBy(() -> element.shouldHave(text("Miller")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul .nonexistent}")
      .hasMessageContaining("Expected: text \"Miller\"")
      .has(screenshot())
      .getCause()
      .isInstanceOf(NoSuchElementException.class)
      .is(webElementNotFound("ul .nonexistent"));
  }

  @Test
  void shouldCondition_WhenCollectionElementByIndex_WithNonExistentCollection() {
    SelenideElement element = $$("ul .nonexistent").get(1);

    assertThatThrownBy(() -> element.shouldHave(text("Miller")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul .nonexistent[1]}")
      .hasMessageContaining("Expected: visible")
      .has(screenshot())
      .getCause()
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessageMatching("Index\\D+1\\D+0");
  }

  @Test
  void shouldCondition_WhenCollectionElementByIndex_WithIndexOutOfRange() {
    SelenideElement element = $$("ul li").get(10);

    assertThatThrownBy(() -> element.shouldHave(text("Miller")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul li[10]}")
      .hasMessageContaining("Expected: text \"Miller\"")
      .has(screenshot())
      .getCause()
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessageMatching("Index\\D+10\\D+2");
  }

  @Test
  void actionWithVisibilityWaiting_WhenCollectionElementByIndex_WithIndexOutOfRange() {
    SelenideElement element = $$("ul li").get(10);

    assertThatThrownBy(() -> element.click())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul li[10]}")
      .hasMessageContaining("Expected: clickable: interactable and enabled")
      .has(screenshot())
      .getCause()
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessageMatching("Index\\D+10\\D+2");
  }

  @Test
  void shouldCondition_WhenCollectionElementByCondition_WithNonExistentCollection() {
    SelenideElement element = $$(".nonexistent").findBy(cssClass("the-expanse"));

    assertThatThrownBy(() -> element.shouldBe(exist))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {.nonexistent.findBy(css class \"the-expanse\")}")
      .hasMessageContaining("Expected: exist")
      .has(screenshot())
      .getCause()
      .isInstanceOf(NoSuchElementException.class)
      .hasMessageStartingWith("Cannot locate an element .nonexistent.findBy(css class \"the-expanse\")");
  }

  @Test
  void shouldCondition_WhenCollectionElementByCondition_WithNotSatisfiedCondition() {
    SelenideElement element = $$("li").findBy(cssClass("nonexistent"));

    assertThatThrownBy(() -> element.shouldBe(visible))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {li.findBy(css class \"nonexistent\")}")
      .hasMessageContaining("Expected: visible")
      .has(screenshot())
      .getCause()
      .isInstanceOf(NoSuchElementException.class)
      .hasMessageStartingWith("Cannot locate an element li.findBy(css class \"nonexistent\")");
  }

  @Test
  void actionWithExistenceWaiting_WhenCollectionElementByCondition_WithNotSatisfiedCondition() {
    SelenideElement element = $$("li").findBy(cssClass("nonexistent"));

    assertThatThrownBy(() -> element.text())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {li.findBy(css class \"nonexistent\")}")
      .hasMessageContaining("Expected: exist")
      .has(screenshot())
      .getCause()
      .isInstanceOf(NoSuchElementException.class)
      .hasMessageStartingWith("Cannot locate an element li.findBy(css class \"nonexistent\")");
  }

  @Test
  void shouldCondition_WhenInnerElement_WithNonExistentOuterElement() {
    SelenideElement element = $(".nonexistent").find(".the-expanse");

    assertThatThrownBy(() -> element.should(appear))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {.nonexistent}")
      .hasMessageContaining("Expected: exist")
      .has(screenshot())
      .getCause()
      .isInstanceOf(NoSuchElementException.class)
      .is(webElementNotFound(".nonexistent"));
  }

  @Test
  void shouldCondition_WhenInnerElement_WithNonExistentInnerElement() {
    SelenideElement element = $("ul").find(".nonexistent");

    assertThatThrownBy(() -> element.shouldBe(visible))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul/.nonexistent}")
      .hasMessageContaining("Expected: visible")
      .has(screenshot())
      .getCause()
      .isInstanceOf(NoSuchElementException.class)
      .is(webElementNotFound(".nonexistent"));
  }

  @Test
  void actionWithVisibilityWaiting_WhenInnerElement_WithNonExistentInnerElement() {
    SelenideElement element = $("ul").find(".nonexistent");

    assertThatThrownBy(() -> element.doubleClick())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul/.nonexistent}")
      .hasMessageContaining("Expected: clickable: interactable and enabled")
      .has(screenshot())
      .getCause()
      .isInstanceOf(NoSuchElementException.class)
      .is(webElementNotFound(".nonexistent"));
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementByConditionInFilteredCollection_WithNonExistentStartCollection() {
    SelenideElement element = $$("ul .nonexistent").
      filterBy(cssClass("the-expanse"))
      .findBy(cssClass("detective"))
      .find("label");

    assertThatThrownBy(() -> element.shouldHave(exactText("detective")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found " +
        "{ul .nonexistent.filter(css class \"the-expanse\").findBy(css class \"detective\")}")
      .hasMessageContaining("Expected: exist")
      .has(screenshot())
      .getCause()
      .isInstanceOf(NoSuchElementException.class)
      .hasMessageStartingWith("Cannot locate an element" +
        " ul .nonexistent.filter(css class \"the-expanse\").findBy(css class \"detective\")");
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementByConditionInFilteredCollection_WithEmptyFilteredCollection() {
    SelenideElement element = $$("ul li").filterBy(cssClass("nonexistent")).findBy(cssClass("detective")).find("label");

    assertThatThrownBy(() -> element.shouldHave(exactText("detective")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul li.filter(css class \"nonexistent\").findBy(css class \"detective\")}")
      .hasMessageContaining("Expected: exist")
      .has(screenshot())
      .getCause()
      .isInstanceOf(NoSuchElementException.class)
      .hasMessageStartingWith("Cannot locate an element ul li.filter(css class \"nonexistent\").findBy(css class \"detective\")");
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementByConditionInFilteredCollection_WithNonExistentOuterElement() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).findBy(cssClass("nonexistent")).find("label");

    assertThatThrownBy(() -> element.shouldHave(exactText("detective")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul li.filter(css class \"the-expanse\").findBy(css class \"nonexistent\")}")
      .hasMessageContaining("css class \"nonexistent\"")
      .has(screenshot())
      .getCause()
      .isInstanceOf(NoSuchElementException.class)
      .hasMessageStartingWith("Cannot locate an element ul li.filter(css class \"the-expanse\").findBy(css class \"nonexistent\")");
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementByConditionInFilteredCollection_WithNonExistentInnerElement() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).findBy(cssClass("detective")).find(".nonexistent");

    assertThatThrownBy(() -> element.shouldHave(exactText("detective")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found " +
        "{ul li.filter(css class \"the-expanse\").findBy(css class \"detective\")/.nonexistent}")
      .hasMessageContaining("Expected: exact text \"detective\"")
      .has(screenshot())
      .getCause()
      .isInstanceOf(NoSuchElementException.class)
      .is(webElementNotFound(".nonexistent"));
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementFoundByIndexInFilteredCollection_WithNonExistentStartCollection() {
    SelenideElement element = $$("ul .nonexistent").filterBy(cssClass("the-expanse")).get(0).find("label");

    assertThatThrownBy(() -> element.shouldHave(exactText("detective")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul .nonexistent.filter(css class \"the-expanse\")[0]}")
      .hasMessageContaining("Expected: visible")
      .has(screenshot())
      .getCause()
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessageMatching("Index: 0");
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementFoundByIndexInFilteredCollection_WithEmptyFilteredCollection() {
    SelenideElement element = $$("ul li").filterBy(cssClass("nonexistent")).get(0).find("label");

    assertThatThrownBy(() -> element.shouldHave(exactText("detective")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul li.filter(css class \"nonexistent\")[0]}")
      .hasMessageContaining("Expected: visible")
      .has(screenshot())
      .getCause()
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessageMatching("Index: 0");
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementFoundByIndexInFilteredCollection_WithIndexOutOfRange() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).get(2).find("label");

    assertThatThrownBy(() -> element.shouldHave(exactText("detective")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul li.filter(css class \"the-expanse\")[2]}")
      .hasMessageContaining("Expected: exist")
      .has(screenshot())
      .getCause()
      .isInstanceOf(IndexOutOfBoundsException.class)
      .hasMessageMatching("Index: 2");
  }

  @Test
  void shouldCondition_WhenInnerElementFromOuterElementFoundByIndexInFilteredCollection_WithNonExistentInnerElement() {
    SelenideElement element = $$("ul li").filterBy(cssClass("the-expanse")).get(0).find(".nonexistent");

    assertThatThrownBy(() -> element.shouldHave(exactText("detective")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul li.filter(css class \"the-expanse\")[0]/.nonexistent}")
      .hasMessageContaining("Expected: exact text \"detective\"")
      .has(screenshot())
      .getCause()
      .isInstanceOf(NoSuchElementException.class)
      .is(webElementNotFound(".nonexistent"));
  }
}
