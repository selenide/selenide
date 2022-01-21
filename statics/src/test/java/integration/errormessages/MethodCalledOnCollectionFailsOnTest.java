package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ListSizeMismatch;
import integration.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static integration.errormessages.Helper.screenshot;
import static integration.errormessages.Helper.webElementNotFound;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class MethodCalledOnCollectionFailsOnTest extends IntegrationTest {
  @BeforeEach
  void openPage() {
    givenHtml(
      "<ul id=\"root\">Hello to:",
      "<li class='the-expanse detective'>Miller</li>",
      "<li class='the-expanse missing'>Julie Mao</li>",
      "</ul>"
    );
    Configuration.timeout = 1;
  }

  @Test
  void shouldCondition_When$$Collection_WithNonExistentWebElements() {
    ElementsCollection collection = $$("ul .nonexistent");

    assertThatThrownBy(() -> collection.shouldHave(exactTexts("Miller", "Julie Mao")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul .nonexistent}")
      .hasMessageContaining("Expected: Exact texts [Miller, Julie Mao]")
      .has(screenshot())
      .hasNoCause();
  }

  @Test
  void shouldCondition_WhenFilteredCollection_On$$CollectionWithNonExistentWebElements() {
    ElementsCollection collection = $$("ul .nonexistent").filter(cssClass("the-expanse"));

    assertThatThrownBy(() -> collection.shouldHave(exactTexts("Miller", "Julie Mao")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul .nonexistent.filter(css class \"the-expanse\")}")
      .hasMessageContaining("Expected: Exact texts [Miller, Julie Mao]")
      .has(screenshot())
      .hasNoCause();
  }

  @Test
  void shouldCondition_WhenFilteredCollection_WithNotSatisfiedCondition() {
    ElementsCollection collection = $$("ul li").filter(cssClass("nonexistent"));

    assertThatThrownBy(() -> collection.shouldHave(exactTexts("Miller", "Julie Mao")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul li.filter(css class \"nonexistent\")}")
      .hasMessageContaining("Expected: Exact texts [Miller, Julie Mao]")
      .has(screenshot())
      .hasNoCause();
  }

  @Test
  void shouldCondition_WhenInnerCollection_WithNonExistentOuterWebElement() {
    ElementsCollection collection = $(".nonexistent").findAll("li");

    assertThatThrownBy(() -> collection.shouldHave(exactTexts("Miller", "Julie Mao")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {.nonexistent/li}")
      .hasMessageContaining("Expected: Exact texts [Miller, Julie Mao]")
      .has(screenshot())
      .getCause()
      .isInstanceOf(NoSuchElementException.class)
      .is(webElementNotFound(".nonexistent"));
  }

  @Test
  void shouldCondition_WhenInnerCollection_WithNonExistentInnerWebElements() {
    ElementsCollection collection = $("ul").findAll(".nonexistent");

    assertThatThrownBy(() -> collection.shouldHave(exactTexts("Miller", "Julie Mao")))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul/.nonexistent}")
      .hasMessageContaining("Expected: Exact texts [Miller, Julie Mao]")
      .has(screenshot())
      .hasNoCause();
  }

  @Test
  void shouldHaveSizeCondition_When$$Collection_WithNotSatisfiedConditionInShould() {
    ElementsCollection collection = $$("ul li");
    assertThatThrownBy(() -> collection.shouldHave(size(3)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: = 3, actual: 2, collection: ul li")
      .has(screenshot())
      .hasNoCause();
  }

  @Test
  void shouldHaveSizeCondition_When$$Collection_WithNonExistentCollection() {
    ElementsCollection collection = $$("ul .nonexistent");

    assertThatThrownBy(() -> collection.shouldHave(size(3)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: = 3, actual: 0, collection: ul .nonexistent")
      .has(screenshot())
      .hasNoCause();
  }

  @Test
  void shouldNotWrap_ElementNotFound_error_twice() {
    assertThatThrownBy(() ->
      $$("ul").findBy(text("NOPE")).shouldBe(visible)
    )
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul.findBy(text \"NOPE\")}")
      .hasMessageContaining("Expected: visible")
      .hasCauseInstanceOf(NoSuchElementException.class)
      .getCause()
      .hasMessageStartingWith("Cannot locate an element ul.findBy(text \"NOPE\")");
  }

  @Test
  void shouldNotWrap_ElementNotFound_error_twice_whenIntermediateElementNotFound() {
    assertThatThrownBy(() ->
      $$("ul").findBy(text("NOPE")).find(byText("YES")).find(byText("NO")).shouldBe(visible)
    )
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {ul.findBy(text \"NOPE\")}")
      .hasMessageContaining("Expected: exist")
      .hasCauseInstanceOf(NoSuchElementException.class)
      .getCause()
      .hasMessageStartingWith("Cannot locate an element ul.findBy(text \"NOPE\")");
  }

  @Test
  void collectionSnapshot() {
    assertThatThrownBy(() -> $$("#root li").snapshot().shouldHave(size(3)))
      .isInstanceOf(ListSizeMismatch.class)
      .hasMessageStartingWith("List size mismatch: expected: = 3, actual: 2, collection: #root li.snapshot(2 elements)");
  }
}
