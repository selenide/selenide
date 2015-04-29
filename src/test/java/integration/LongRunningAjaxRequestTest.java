package integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertTrue;

public class LongRunningAjaxRequestTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("long_ajax_request.html");
    timeout = averageSeleniumCommandDuration * 30;
    $("#loading").shouldNot(exist);
    $(byText("Run long request")).click();
    $("#loading").shouldBe(visible).shouldHave(text("Loading..."));
  }

  @Test
  public void dollarWaitsForElement() {
    $(byText("Result 1")).shouldBe(visible);
  }

  @Test
  public void dollarWaitsForElementWithIndex() {
    $("#results li", 1).shouldHave(text("Result 2"));
  }

  @Test
  public void dollarWaitsUntilElementDisappears() {
    $(byText("Loading...")).should(exist);
    $(byText("Loading...")).should(disappear);
    $(byText("Loading...")).shouldNot(exist);
  }

  @Test
  public void userCanWaitUntilConditionIsMet() {
    timeout = 10;
    $(byText("Result 2")).waitUntil(visible, 3000);
    assertTrue($(byText("Result 2")).isDisplayed());
  }

  @Test
  @SuppressWarnings("deprecation")
  public void userCanWaitWhileConditionIsMet() {
    timeout = 10;
    $(byText("Result 2")).waitWhile(notPresent, 3000);
    assertTrue($(byText("Result 2")).isDisplayed());
  }

  @Test
  public void dollarWithParentWaitsUntilElementDisappears() {
    $($("#results"), "span#loading").should(exist);
    $($("#results"), "span#loading").shouldNot(exist);
  }

  @Test
  public void dollarWithParentAndIndexWaitsUntilElementDisappears() {
    $($("#results"), "span#loading", 0).should(exist);
    $($("#results"), "span#loading", 0).shouldNot(exist);
    $($("#results"), "span#loading", 666).shouldNot(exist);
  }

  @Test(expected = AssertionError.class)
  public void waitingTimeout() {
    $("#non-existing-element").should(exist);
  }

  @Test
  public void shouldWaitsForCondition() {
    $("#results").shouldHave(text("Result 1"));
  }

  @Test
  public void shouldWaitsForAllConditions() {
    $("#results").shouldHave(text("Result 1"), text("Result 2"));
  }

  @Test
  public void shouldNotExist() {
    $("#non-existing-element").shouldNot(exist);
    $("#non-existing-element", 7).shouldNot(exist);
    $(By.linkText("non-existing-link")).shouldNot(exist);
    $(By.linkText("non-existing-link"), 8).shouldNot(exist);
  }

  @Test
  public void findWaitsForConditions() {
    $("#results").find(byText("non-existing element")).shouldNot(exist);
    $("#results").find(byText("non-existing element"), 3).shouldNot(exist);

    $("#results").find(byText("Loading...")).shouldNot(exist);
    $("#results").find(byText("Loading..."), 0).shouldNot(exist);
  }

  @Test
  public void shouldNotExistWithinParentElement() {
    $($("body"), "#non-existing-element").shouldNot(exist);
    $($("body"), "#non-existing-element", 4).shouldNot(exist);
  }

  @Test
  public void shouldNotBeVisible() {
    $("#non-existing-element").shouldNotBe(visible);
    $("#non-existing-element", 7).shouldNotBe(visible);
    $(By.linkText("non-existing-link")).shouldNotBe(visible);
    $(By.linkText("non-existing-link"), 8).shouldNotBe(visible);
  }

  @Test
  public void shouldNotBeVisibleWithinParentElement() {
    $($("body"), "#non-existing-element").shouldNotBe(visible);
    $($("body"), "#non-existing-element", 4).shouldNotBe(visible);
  }
}
