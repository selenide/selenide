package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.and;
import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.appears;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.be;
import static com.codeborne.selenide.Condition.checked;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.cssValue;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.editable;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.focused;
import static com.codeborne.selenide.Condition.have;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.href;
import static com.codeborne.selenide.Condition.image;
import static com.codeborne.selenide.Condition.interactable;
import static com.codeborne.selenide.Condition.match;
import static com.codeborne.selenide.Condition.matchText;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.type;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ConditionsTest extends ITest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void andShouldCheckConditions() {
    $("#multirowTable").should(and("visible && table", be(visible), have(cssClass("table")))); // both true
    $("#multirowTable").shouldNot(and("visible && list", be(visible), have(cssClass("list")))); // first true
    $("#multirowTable").shouldNot(and("hidden && table", be(hidden), have(cssClass("table")))); // second true
    $("#multirowTable").shouldNot(and("hidden && list", be(hidden), have(cssClass("list")))); // both false
  }

  @Test
  void orShouldCheckConditions() {
    $("#multirowTable").should(or("visible || table", be(visible), have(cssClass("table")))); // both true
    $("#multirowTable").should(or("visible || list", be(visible), have(cssClass("table1")))); // first true
    $("#multirowTable").should(or("hidden || table", be(hidden), have(cssClass("table")))); // second true
    $("#multirowTable").shouldNot(or("hidden || list", be(hidden), have(cssClass("list")))); // both false
  }

  @Test
  void actualValueOfOr() {
    assertThatThrownBy(() -> $("#multirowTable").shouldBe(or("satisfied",
      be(hidden),
      not(visible),
      have(cssClass("list")),
      have(text("nope")),
      be(disabled),
      not(enabled),
      have(attribute("foo", "bar")),
      have(href("https://nope.ee"))
    ))).isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be satisfied: be hidden or not visible or have css class \"list\"" +
        " or have text \"nope\" or be disabled or not enabled or have attribute foo=\"bar\"" +
        " or have attribute href=\"https://nope.ee\" {#multirowTable}")
      .hasMessageContaining("Actual value: visible, visible, class=\"table multirow_table\", text=\"Chack Norris\n" +
        "Chack L'a Baskerville\", enabled, enabled, foo=\"\", href=\"\"");
  }

  @Test
  void actualValueOfAnd() {
    assertThatThrownBy(() -> $("#multirowTable").shouldBe(and("satisfied",
      be(visible),
      not(hidden),
      have(cssClass("multirow_table")),
      have(partialText("Baskerville")),
      be(enabled),
      not(disabled),
      have(attribute("class", "table multirow_table")),
      have(href("https://nope.lt"))
    ))).isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be satisfied: be visible and not hidden and have css class \"multirow_table\"" +
        " and have partial text \"Baskerville\" and be enabled and not disabled and have attribute class=\"table multirow_table\"" +
        " and have attribute href=\"https://nope.lt\" {#multirowTable}")
      .hasMessageContaining("Actual value: href=\"\"");
  }

  @Test
  void orShouldReportAllConditions() {
    assertThatThrownBy(() ->
      $("#multirowTable").shouldBe(or("non-active", be(disabled), have(cssClass("inactive"))))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be non-active: be disabled or have css class \"inactive\" {#multirowTable}")
      .hasMessageContaining("Actual value: enabled, class=\"table multirow_table\"");
  }

  @Test
  void orShouldReportAllConditionsWithActualValues() {
    assertThatThrownBy(() ->
      $("#multirowTable").shouldHave(or("class || border", attribute("class", "foo"), attribute("border", "bar")))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith(
        "Element should have class || border: attribute class=\"foo\" or attribute border=\"bar\" {#multirowTable}")
      .hasMessageContaining("Actual value: class=\"table multirow_table\", border=\"1\"");
  }

  @Test
  void notShouldCheckConditions() {
    $("#multirowTable").should(be(visible));
    $("#multirowTable").should(not(be(hidden)));
  }

  @Test
  void userCanUseOrCondition() {
    Condition one_of_conditions = or("baskerville", partialText("Basker"), partialText("Walle"));
    $("#baskerville").shouldBe(one_of_conditions);

    Condition all_of_conditions = or("baskerville", partialText("Basker"), partialText("rville"));
    $("#baskerville").shouldBe(all_of_conditions);

    Condition none_of_conditions = or("baskerville", partialText("pasker"), partialText("wille"));
    $("#baskerville").shouldNotBe(none_of_conditions);
  }

  @Test
  void matchWithCustomPredicateShouldCheckCondition() {
    $("#multirowTable").should(match("border=1", el -> el.getAttribute("border").equals("1")));
  }

  @Test
  void matchWithPredicateShouldReportErrorMessage() {
    assertThatThrownBy(() ->
      $("#multirowTable").should(match("tag=input", el -> el.getTagName().equals("input1")))
    )
      .hasMessageStartingWith("Element should match 'tag=input' predicate. {#multirowTable}")
      .hasMessageNotContaining("Actual value");
  }

  @Test
  void matchWithShouldNotPredicateReportErrorMessage() {
    assertThatThrownBy(() ->
      $("#multirowTable").shouldNot(match("border=1", el -> el.getAttribute("border").equals("1")))
    )
      .hasMessageStartingWith(
        "Element should not match 'border=1' predicate. {#multirowTable}");
  }

  @Test
  void actual_value_focused() {
    assertThatThrownBy(() -> $("#multirowTable").shouldBe(focused))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be focused {#multirowTable}")
      .hasMessageContaining("Actual value: Focused element: <body id>, current element: <table id=\"multirowTable\">");
  }

  @Test
  void actual_value_attribute() {
    assertThatThrownBy(() -> $("#multirowTable").shouldHave(attribute("data-test-id")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have attribute data-test-id {#multirowTable}")
      .hasMessageContaining("Actual value: data-test-id");
  }

  @Test
  void actual_value_attribute_with_value() {
    assertThatThrownBy(() -> $("#multirowTable").shouldHave(attribute("class", "foo")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have attribute class=\"foo\" {#multirowTable}")
      .hasMessageContaining("Actual value: class=\"table multirow_table\"");
  }

  @Test
  void actual_value_href() {
    assertThatThrownBy(() -> $("#ajax-button").shouldHave(href("take-me-to-church.html")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have attribute href=\"take-me-to-church.html\" {#ajax-button}")
      .hasMessageContaining("Actual value: href=\"" + getBaseUrl() + "/long_ajax_request.html\"");
  }

  @Test
  void actual_value_cssClass() {
    assertThatThrownBy(() -> $("#multirowTable").shouldHave(cssClass("single_row_table")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have css class \"single_row_table\" {#multirowTable}")
      .hasMessageContaining("Actual value: class=\"table multirow_table\"");
  }

  @Test
  void actual_value_visible() {
    assertThatThrownBy(() -> $("#theHiddenElement").shouldBe(visible))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be visible {#theHiddenElement}")
      .hasMessageContaining("Actual value: hidden");
  }

  @Test
  void actual_value_hidden() {
    assertThatThrownBy(() -> $("h1").shouldBe(hidden))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be hidden {h1}")
      .hasMessageContaining("Actual value: visible");
  }

  @Test
  void actual_value_enabled() {
    assertThatThrownBy(() -> $("#logout").shouldBe(enabled))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be enabled {#logout}")
      .hasMessageContaining("Actual value: disabled");
  }

  @Test
  void actual_value_disabled() {
    assertThatThrownBy(() -> $("#login").shouldBe(disabled))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be disabled {#login}")
      .hasMessageContaining("Actual value: enabled");
  }

  @Test
  void actual_value_checked() {
    assertThatThrownBy(() -> $("[name=rememberMe]").shouldBe(checked))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be checked {[name=rememberMe]}")
      .hasMessageContaining("Actual value: unchecked");
  }

  @Test
  void actual_value_text() {
    assertThatThrownBy(() -> $("h1").shouldHave(text("Page with dropdowns")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have text \"Page with dropdowns\" {h1}")
      .hasMessageContaining("Actual value: text=\"Page with selects\"");
  }

  @Test
  void actual_value_exactText() {
    assertThatThrownBy(() -> $("h1").shouldHave(exactText("Page with dropdowns")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have exact text \"Page with dropdowns\" {h1}")
      .hasMessageContaining("Actual value: text=\"Page with selects\"");
  }

  @Test
  void actual_value_be() {
    assertThatThrownBy(() -> $("h1").should(be(disabled)))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be disabled {h1}")
      .hasMessageContaining("Actual value: enabled");
  }

  @Test
  void actual_value_have() {
    assertThatThrownBy(() -> $("h1").should(have(ownText("Page with dropdowns"))))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have own text \"Page with dropdowns\" {h1}")
      .hasMessageContaining("Actual value: text=\"Page with selects\"");
  }

  @Test
  void actual_value_cssValue() {
    assertThatThrownBy(() -> $("#theHiddenElement").shouldHave(cssValue("display", "block")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have css value display=block {#theHiddenElement}")
      .hasMessageContaining("Actual value: display=none");
  }

  @Test
  void actual_value_value() {
    assertThatThrownBy(() -> $("#age").shouldHave(value("21")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have value=\"21\" {#age}")
      .hasMessageContaining("Actual value: value=\"18\"");
  }

  @Test
  void actual_value_exist() {
    assertThatThrownBy(() -> $("#age").shouldNot(exist))
      .isInstanceOf(ElementShouldNot.class)
      .hasMessageStartingWith("Element should not exist {#age}")
      .hasMessageContaining("Element: '<input id=\"age\"")
      .hasMessageContaining("Actual value: exists");
  }

  @Test
  void actual_value_not_exist() {
    assertThatThrownBy(() -> $("#missing").should(exist))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#missing}")
      .hasMessageContaining("Expected: exist")
      .hasMessageNotContaining("Actual value");
  }

  @Test
  void appear_errorMessage() {
    assertThatThrownBy(() -> $("#theHiddenElement").should(appear))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be visible {#theHiddenElement}");
  }

  @Test
  void appears_errorMessage() {
    assertThatThrownBy(() -> $("#theHiddenElement").should(appears))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be visible {#theHiddenElement}");
  }

  @Test
  void disappear_errorMessage() {
    assertThatThrownBy(() -> $("h1").should(disappear))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be hidden {h1}");
  }

  @Test
  void interactable_errorMessage() {
    assertThatThrownBy(() -> $("#theHiddenElement").shouldBe(interactable))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be interactable {#theHiddenElement}");
  }

  @Test
  void editable_errorMessage() {
    assertThatThrownBy(() -> $("#theHiddenElement").shouldBe(editable))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be editable {#theHiddenElement}");
  }

  @Test
  void type_errorMessage() {
    assertThatThrownBy(() -> $("[name=domain]").shouldHave(type("tel")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have attribute type=\"tel\" {[name=domain]}");
  }

  @Test
  void empty_errorMessage() {
    assertThatThrownBy(() -> $("h1").shouldBe(empty))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be empty: attribute value=\"\" and exact text \"\" {h1}");
  }

  @Test
  void matchText_errorMessage() {
    assertThatThrownBy(() -> $("h1").should(matchText("Wrong text")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should match text \"Wrong text\" {h1}");
  }

  @Test
  void image_errorMessage() {
    assertThatThrownBy(() -> $("h1").shouldBe(image))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be image {h1}");
  }
}
