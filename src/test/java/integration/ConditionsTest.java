package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.and;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.be;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.have;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.match;
import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.text;
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
  void orShouldReportAllConditions() {
    assertThatThrownBy(() ->
      $("#multirowTable").shouldBe(or("non-active", be(disabled), have(cssClass("inactive"))))
    )
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be non-active: be disabled or have css class 'inactive' {#multirowTable}");
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
    $("#multirowTable").should(Condition.not(be(hidden)));
  }

  @Test
  void userCanUseOrCondition() {
    Condition one_of_conditions = or("baskerville", text("Basker"), text("Walle"));
    $("#baskerville").shouldBe(one_of_conditions);

    Condition all_of_conditions = or("baskerville", text("Basker"), text("rville"));
    $("#baskerville").shouldBe(all_of_conditions);

    Condition none_of_conditions = or("baskerville", text("pasker"), text("wille"));
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
      .hasMessageStartingWith(
        "Element should match 'tag=input' predicate. {#multirowTable}");
  }

  @Test
  void matchWithShouldNotPredicateReportErrorMessage() {
    assertThatThrownBy(() ->
      $("#multirowTable").shouldNot(match("border=1", el -> el.getAttribute("border").equals("1")))
    )
      .hasMessageStartingWith(
        "Element should not match 'border=1' predicate. {#multirowTable}");
  }
}
