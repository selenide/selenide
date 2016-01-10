package integration;

import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;

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
    $(byXpath("//h1")).shouldHave(text("Page without JQuery"));
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
}
