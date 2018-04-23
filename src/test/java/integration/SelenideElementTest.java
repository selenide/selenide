package integration;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SelenideElementTest extends IntegrationTest {
  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void selenideElementImplementsWrapsElement() {
    WebElement wrappedElement = $("#login").getWrappedElement();
    assertNotNull(wrappedElement);
    assertEquals("login", wrappedElement.getAttribute("id"));
  }

  @Test
  public void selenideElementImplementsWrapsWebdriver() {
    WebDriver wrappedDriver = $("#login").getWrappedDriver();
    assertNotNull(wrappedDriver);
    String currentUrl = wrappedDriver.getCurrentUrl();
    assertTrue("Current URL is " + currentUrl,
            currentUrl.contains("page_with_selects_without_jquery.html"));
  }

  @Test // @Ignore(value = "probably a bug in Selenide")
  public void selenideElementChainedWithByTextSelector() {
    $("#status").$(withText("Smith")).shouldBe(visible);
    $("#status").$(byText("Bob Smith")).shouldBe(visible);
  }

  @Test
  @Ignore(value = "It fails, please check if it is right")
  public void selenideElementChainedElementByTextWhenTextIsDirectContentOfTheParent() {
    // e.g. <div id="radioButton><img/>Мастер<div/></div>
    $("#radioButtons").$(withText("Мастер")).shouldBe(visible); //Fails
  }
}
