package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.*;

public class SelectorsTest extends IntegrationTest {
  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void canFindElementByName() {
    $(byName("domain")).should(exist);
  }

  @Test
  public void canFindElementByXPath() {
    $(byXpath("//h1")).shouldHave(text("Page with selects"));
    $(byXpath("//*[@name='domain']")).shouldBe(visible);
  }

  @Test
  public void canFindElementByLinkText() {
    $(byLinkText("Options with 'apostrophes' and \"quotes\"")).shouldHave(text("Options with 'apostrophes' and \"quotes\""));
  }

  @Test
  public void canFindElementByPartialLinkText() {
    $(byPartialLinkText("'apostrophes")).shouldHave(text("Options with 'apostrophes' and \"quotes\""));
    $(byPartialLinkText("quotes\"")).shouldHave(text("Options with 'apostrophes' and \"quotes\""));
  }

  @Test
  public void canFindElementById() {
    $(byId("status")).shouldHave(text("Username:"));
  }

  @Test
  public void canFindSelenideElementByXpath() {
    $x("//h1").shouldHave(text("Page with selects"));
    $x("//*[@id='status']").shouldHave(text("Username:"));
    $x("//*[@name='domain']").shouldBe(visible);
  }

  @Test
  public void canFindElementsCollectionByXpath() {
    $$x("//h1").get(0).shouldHave(text("Page with selects"));
    $$x("//*[@id='status']").get(0).shouldHave(text("Username:"));
    $$x("//*[@name='domain']").get(0).shouldBe(visible);
  }

  @Test
  public void canFindChildSelenideElementByXpath() {
    SelenideElement parent = $x("//div[@id='radioButtons']");
    parent.$x("./h2").shouldHave(text("Radio buttons"));
  }

  @Test
  public void canFindChildElementsCollectionByXpath() {
    SelenideElement parent = $x("//table[@id='multirowTable']");
    parent.$$x(".//tr").shouldHaveSize(2);
  }

  @Test
  public void canFindNthChildSelenideElementByXpath() {
    SelenideElement parent = $x("//table[@id='multirowTable']");
    parent.$x(".//tr", 0).shouldHave(text("Chack Norris"));
  }
}
