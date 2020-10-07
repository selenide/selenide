package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SelenideElementTest extends ITest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void selenideElementImplementsWrapsElement() {
    WebElement wrappedElement = $("#login").getWrappedElement();
    assertNotNull(wrappedElement);
    assertEquals("login", wrappedElement.getAttribute("id"));
  }

  @Test
  void selenideElementImplementsWrapsWebdriver() {
    WebDriver wrappedDriver = $("#login").getWrappedDriver();
    assertNotNull(wrappedDriver);
    String currentUrl = wrappedDriver.getCurrentUrl();
    assertTrue(
      currentUrl.contains("page_with_selects_without_jquery.html"), "Current URL is " + currentUrl);
  }

  @Test
    // @Ignore(value = "probably a bug in Selenide")
  void selenideElementChainedWithByTextSelector() {
    $("#status").$(withText("Smith")).shouldBe(visible);
    $("#status").$(byText("Bob Smith")).shouldBe(visible);
  }

  @Test
  @Disabled(value = "It fails, please check if it is right")
  void selenideElementChainedElementByTextWhenTextIsDirectContentOfTheParent() {
    // e.g. <div id="radioButton><img/>Мастер<div/></div>
    $("#radioButtons").$(withText("Мастер")).shouldBe(visible); //Fails
  }
}
