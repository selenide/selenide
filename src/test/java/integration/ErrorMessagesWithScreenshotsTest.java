package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Configuration.reportsFolder;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ErrorMessagesWithScreenshotsTest extends IntegrationTest {
  private String reportsUrl;
  
  @Before
  public final void setTimeout() {
    timeout = 0;
    openFile("page_with_selects_without_jquery.html");
  }

  @Before
  public void mockScreenshots() {
    reportsUrl = Configuration.reportsUrl;
    Configuration.reportsUrl = "http://ci.org/";
    Screenshots.screenshots = new ScreenShotLaboratory() {
      @Override
      public String takeScreenShot() {
        return new File(reportsFolder, "1.jpg").getAbsolutePath();
      }
    };
  }

  @After
  public void restoreScreenshots() {
    Configuration.reportsUrl = reportsUrl;
    Screenshots.screenshots = new ScreenShotLaboratory();
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
          e.getMessage().contains("Element not found {#nonexisting-form}"));
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
          e.getMessage().contains("Element not found {thead}"));
      assertEquals("http://ci.org/build/reports/tests/1.jpg", e.getScreenshot());
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
          e.getMessage().contains("Element not found {<table id=\"multirowTable\">/thead"));
      assertEquals("http://ci.org/build/reports/tests/1.jpg", e.getScreenshot());
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
          e.getMessage().contains("Element not found {.second_row}"));
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
