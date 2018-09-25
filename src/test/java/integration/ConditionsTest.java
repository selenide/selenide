package integration;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.and;
import static com.codeborne.selenide.Condition.be;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.have;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

class ConditionsTest extends ITest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void andShouldCheckConditions() {
    $("#multirowTable").should(and("both true", be(visible), have(cssClass("table"))));
    $("#multirowTable").shouldNot(and("first true", be(visible), have(cssClass("table1"))));
    $("#multirowTable").shouldNot(and("second true", be(hidden), have(cssClass("table"))));
    $("#multirowTable").shouldNot(and("both false", be(hidden), have(cssClass("table1"))));
  }

  @Test
  void orShouldCheckConditions() {
    $("#multirowTable").should(or("both true", be(visible), have(cssClass("table"))));
    $("#multirowTable").should(or("first true", be(visible), have(cssClass("table1"))));
    $("#multirowTable").should(or("second true", be(hidden), have(cssClass("table"))));
    $("#multirowTable").shouldNot(or("both false", be(hidden), have(cssClass("table1"))));
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
}
