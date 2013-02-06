package com.codeborne.selenide.integrationtests;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.DOM.*;
import static com.codeborne.selenide.Navigation.navigateToAbsoluteUrl;
import static com.codeborne.selenide.Selectors.byText;
import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LongRunningAjaxRequestTest {
  @Before
  public void openTestPage() {
    defaultWaitingTimeout = 2500;
    navigateToAbsoluteUrl(currentThread().getContextClassLoader().getResource("long_ajax_request.html"));
    $(byText("Run long request")).click();
    $(byText("Loading...")).should(exist);
    $("#loading").shouldHave(text("Loading"), text("..."));
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
    $(byText("Loading...")).shouldNot(exist);
  }

  @Test
  public void userCanWaitUntilConditionIsMet() {
    defaultWaitingTimeout = 1000;
    assertFalse(existsAndVisible(byText("Result 2")));
    $(byText("Result 2")).waitUntil(visible, 3000);
    assertTrue(existsAndVisible(byText("Result 2")));
  }

  @Test
  public void userCanWaitWhileConditionIsMet() {
    defaultWaitingTimeout = 1000;
    assertFalse(existsAndVisible(byText("Result 2")));
    $(byText("Result 2")).waitWhile(notPresent, 3000);
    assertTrue(existsAndVisible(byText("Result 2")));
  }

  @Test
  public void dollarWithParentWaitsUntilElementDisappears() {
    $($("#results"), "span").should(exist);
    $($("#results"), "span").shouldNot(exist);
  }

  @Test
  public void dollarWithParentAndIndexWaitsUntilElementDisappears() {
    $($("#results"), "span", 0).should(exist);
    $($("#results"), "span", 0).shouldNot(exist);
    $($("#results"), "span", 666).shouldNot(exist);
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
    $($(By.tagName("body")), "#non-existing-element").shouldNot(exist);
    $($(By.tagName("body")), "#non-existing-element", 4).shouldNot(exist);
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
    $($(By.tagName("body")), "#non-existing-element").shouldNotBe(visible);
    $($(By.tagName("body")), "#non-existing-element", 4).shouldNotBe(visible);
  }
}
