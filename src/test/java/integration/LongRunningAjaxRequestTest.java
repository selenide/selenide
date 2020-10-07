package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class LongRunningAjaxRequestTest extends ITest {
  @BeforeEach
  void openTestPage() {
    setTimeout(4000);
    openFile("long_ajax_request.html");
    $("#loading").shouldNot(exist);
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
    $(byText("Loading...")).should(exist);
    $(byText("Loading...")).should(disappear);
    $(byText("Loading...")).shouldNot(exist);
  }

  @Test
  void userCanWaitUntilConditionIsMet() {
    $(byText("Result 2")).waitUntil(visible, 3000);
    assertThat($(byText("Result 2")).isDisplayed()).isTrue();
  }

  @Test
  void dollarWithParentWaitsUntilElementDisappears() {
    $("#results").$("span#loading").should(exist);
    $("#results").$("span#loading").shouldNot(exist);
  }

  @Test
  void dollarWithParentAndIndexWaitsUntilElementDisappears() {
    $("#results").$("span#loading", 0).should(exist);
    $("#results").$("span#loading", 0).shouldNot(exist);
    $("#results").$("span#loading", 666).shouldNot(exist);
  }

  @Test
  void waitingTimeout() {
    assertThatThrownBy(() -> $("#non-existing-element").should(exist))
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
    $("#non-existing-element").shouldNot(exist);
    $("#non-existing-element", 7).shouldNot(exist);
    $(By.linkText("non-existing-link")).shouldNot(exist);
    $(By.linkText("non-existing-link"), 8).shouldNot(exist);
  }

  @Test
  void findWaitsForConditions() {
    $("#results").find(byText("non-existing element")).shouldNot(exist);
    $("#results").find(byText("non-existing element"), 3).shouldNot(exist);

    $("#results").find(byText("Loading...")).shouldNot(exist);
    $("#results").find(byText("Loading..."), 0).shouldNot(exist);
  }

  @Test
  void shouldNotExistWithinParentElement() {
    $("body").$("#non-existing-element").shouldNot(exist);
    $("body").$("#non-existing-element", 4).shouldNot(exist);
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
