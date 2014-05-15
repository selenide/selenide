package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ErrorMessagesWithScreenshotsTest extends IntegrationTest {
  @Before
  public final void setTimeout() {
    timeout = 0;
    openFile("page_with_selects_without_jquery.html");
  }

  @After
  public final void restoreTimeout() {
    timeout = 4000;
  }

  @Test
  public void parentNotFound() {
    try {
      $("#nonexisting-form")
          .findAll(byText("mymail@gmail.com"))
          .find(cssClass("trash"))
          .shouldBe(visible);
      fail();
    } catch (ElementNotFound e) {
      assertTrue("Actual error message: " + e.getMessage(),
          e.getMessage().contains("Element not found {By.selector: #nonexisting-form}"));
    }
  }

  @Test
  public void itShouldBeReportedWhichParentElementIsNotFound() {
    try {
      $("#multirowTable")
          .find("thead")
          .find(byText("mymail@gmail.com"))
          .find(".trash")
          .shouldBe(visible);
      fail();
    } catch (ElementNotFound e) {
      assertTrue("Actual error message: " + e.getMessage(),
          e.getMessage().contains("Element not found {By.selector: thead}"));
    }
  }

  @Test
  public void itShouldBeReportedIfParentCollectionIsNotFound() {
    try {
      $("#multirowTable")
          .findAll("thead")
          .findBy(text("mymail@gmail.com"))
          .find(".trash")
          .shouldBe(visible);
      fail();
    } catch (ElementNotFound e) {
      assertTrue("Actual error message: " + e.getMessage(),
          e.getMessage().contains("Element not found {<table id=multirowTable>/thead"));
    }
  }


  @Test
  public void elementNotFoundInsideParent() {
    try {
      $("#multirowTable")
          .findAll("tbody tr")
          .findBy(text("Norris"))
          .find(".second_row")
          .shouldBe(visible);
      fail();
    } catch (ElementNotFound e) {
      assertTrue("Actual error message: " + e.getMessage(),
          e.getMessage().contains("Element not found {By.selector: .second_row}"));
    }
  }

  @Test
  public void elementShouldNotBeFoundAndParentAlsoNotFound() {
    $("#multirowTable")
        .find("theeeead")
        .find(".second_row")
        .shouldNotBe(visible);
  }
}
