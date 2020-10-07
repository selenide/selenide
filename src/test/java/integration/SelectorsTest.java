package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byId;
import static com.codeborne.selenide.Selectors.byLinkText;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selectors.byPartialLinkText;
import static com.codeborne.selenide.Selectors.byXpath;

final class SelectorsTest extends ITest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canFindElementByName() {
    $(byName("domain")).should(exist);
  }

  @Test
  void canFindElementByXPath() {
    $(byXpath("//h1")).shouldHave(text("Page with selects"));
    $(byXpath("//*[@name='domain']")).shouldBe(visible);
  }

  @Test
  void canFindElementByLinkText() {
    $(byLinkText("Options with 'apostrophes' and \"quotes\"")).shouldHave(text("Options with 'apostrophes' and \"quotes\""));
  }

  @Test
  void canFindElementByPartialLinkText() {
    $(byPartialLinkText("'apostrophes")).shouldHave(text("Options with 'apostrophes' and \"quotes\""));
    $(byPartialLinkText("quotes\"")).shouldHave(text("Options with 'apostrophes' and \"quotes\""));
  }

  @Test
  void byAttributeEscapesQuotes() {
    $(byAttribute("value", "john mc'lain")).shouldHave(attribute("value", "john mc'lain"));
    $(byAttribute("value", "arnold \"schwarzenegger\"")).shouldHave(attribute("value", "arnold \"schwarzenegger\""));
    $("#denzel-washington").shouldHave(attribute("value", "denzel \\\\\"equalizer\\\\\" washington"));
    $(byAttribute("value", "denzel \\\\\"equalizer\\\\\" washington"))
      .shouldHave(attribute("value", "denzel \\\\\"equalizer\\\\\" washington"))
      .shouldHave(text("Denzel Washington"));
  }

  @Test
  void canFindElementById() {
    $(byId("status")).shouldHave(text("Username:"));
  }

  @Test
  void canFindSelenideElementByXpath() {
    $x("//h1").shouldHave(text("Page with selects"));
    $x("//*[@id='status']").shouldHave(text("Username:"));
    $x("//*[@name='domain']").shouldBe(visible);
  }

  @Test
  void canFindElementsCollectionByXpath() {
    $$x("//h1").get(0).shouldHave(text("Page with selects"));
    $$x("//*[@id='status']").get(0).shouldHave(text("Username:"));
    $$x("//*[@name='domain']").get(0).shouldBe(visible);
  }

  @Test
  void canFindChildSelenideElementByXpath() {
    SelenideElement parent = $x("//div[@id='radioButtons']");
    parent.$x("./h2").shouldHave(text("Radio buttons"));
  }

  @Test
  void canFindChildElementsCollectionByXpath() {
    SelenideElement parent = $x("//table[@id='multirowTable']");
    parent.$$x(".//tr").shouldHaveSize(2);
  }

  @Test
  void canFindNthChildSelenideElementByXpath() {
    SelenideElement parent = $x("//table[@id='multirowTable']");
    parent.$x(".//tr", 0).shouldHave(text("Chack Norris"));
  }
}
