package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.existInDOM;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;

class LongRunningAjaxRequestTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("long_ajax_request.html");
    $("#loading").shouldNot(existInDOM);
    $(byText("Run long request")).click();
  }

  @Test
  void dollarWaitsForElement() {
    $(byText("Result 1")).shouldBe(visible);
  }

  @Test
  void dollarWaitsForElementWithIndex() {
    $("#results li", 1).shouldHave(text("Result 2"));
  }

  @Test
  void dollarWaitsUntilElementDisappears() {
    $(byText("Loading...")).should(existInDOM);
    $(byText("Loading...")).should(disappear);
    $(byText("Loading...")).shouldNot(existInDOM);
  }

  @Test
  void userCanWaitUntilConditionIsMet() {
    $(byText("Result 2")).waitUntil(visible, 3000);
    assertThat($(byText("Result 2")).isDisplayed()).isTrue();
  }

  @Test
  void dollarWithParentWaitsUntilElementDisappears() {
    $("#results").$("span#loading").should(existInDOM);
    $("#results").$("span#loading").shouldNot(existInDOM);
  }

  @Test
  void dollarWithParentAndIndexWaitsUntilElementDisappears() {
    $("#results").$("span#loading", 0).should(existInDOM);
    $("#results").$("span#loading", 0).shouldNot(existInDOM);
    $("#results").$("span#loading", 666).shouldNot(existInDOM);
  }

  @Test
  void waitingTimeout() {
    assertThatThrownBy(() -> $("#non-existing-element").should(existInDOM))
      .isInstanceOf(AssertionError.class);
  }

  @Test
  void shouldWaitsForCondition() {
    $("#results").shouldHave(text("Result 1"));
  }

  @Test
  void shouldWaitsForAllConditions() {
    $("#results").shouldHave(text("Result 1"), text("Result 2"));
  }

  @Test
  void shouldNotExist() {
    $("#non-existing-element").shouldNot(existInDOM);
    $("#non-existing-element", 7).shouldNot(existInDOM);
    $(By.linkText("non-existing-link")).shouldNot(existInDOM);
    $(By.linkText("non-existing-link"), 8).shouldNot(existInDOM);
  }

  @Test
  void findWaitsForConditions() {
    $("#results").find(byText("non-existing element")).shouldNot(existInDOM);
    $("#results").find(byText("non-existing element"), 3).shouldNot(existInDOM);

    $("#results").find(byText("Loading...")).shouldNot(existInDOM);
    $("#results").find(byText("Loading..."), 0).shouldNot(existInDOM);
  }

  @Test
  void shouldNotExistWithinParentElement() {
    $("body").$("#non-existing-element").shouldNot(existInDOM);
    $("body").$("#non-existing-element", 4).shouldNot(existInDOM);
  }

  @Test
  void shouldNotBeVisible() {
    $("#non-existing-element").shouldNotBe(visible);
    $("#non-existing-element", 7).shouldNotBe(visible);
    $(By.linkText("non-existing-link")).shouldNotBe(visible);
    $(By.linkText("non-existing-link"), 8).shouldNotBe(visible);
  }

  @Test
  void shouldNotBeVisibleWithinParentElement() {
    $("body").$("#non-existing-element").shouldNotBe(visible);
    $("body").$("#non-existing-element", 4).shouldNotBe(visible);
  }
}
