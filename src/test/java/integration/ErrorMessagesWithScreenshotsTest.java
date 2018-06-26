package integration;

import java.io.File;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.browser;
import static com.codeborne.selenide.Configuration.reportsFolder;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.fail;

class ErrorMessagesWithScreenshotsTest extends IntegrationTest {
  private String reportsUrl;

  @BeforeEach
  final void setTimeout() {
    timeout = 0;
    openFile("page_with_selects_without_jquery.html");
  }

  @BeforeEach
  void mockScreenshots() {
    reportsUrl = Configuration.reportsUrl;
    Configuration.reportsUrl = "http://ci.org/";
    Screenshots.screenshots = new ScreenShotLaboratory() {
      @Override
      public String takeScreenShot() {
        return new File(reportsFolder, "1.jpg").getAbsolutePath();
      }
    };
  }

  @AfterEach
  void restoreScreenshots() {
    Configuration.reportsUrl = reportsUrl;
    Screenshots.screenshots = new ScreenShotLaboratory();
  }

  @Test
  void parentNotFound() {
    try {
      $("#nonexisting-form")
        .findAll(byText("mymail@gmail.com"))
        .find(cssClass("trash"))
        .shouldBe(visible);
      fail();
    } catch (ElementNotFound e) {
      Assertions.assertTrue(
        e.getMessage().contains("Element not found {#nonexisting-form}"), "Actual error message: " + e.getMessage());
    }
  }

  @Test
  void itShouldBeReportedWhichParentElementIsNotFound() {
    try {
      $("#multirowTable")
        .find("thead")
        .find(byText("mymail@gmail.com"))
        .find(".trash")
        .shouldBe(visible);
      fail();
    } catch (ElementNotFound e) {
      Assertions.assertTrue(
        e.getMessage().contains("Element not found {thead}"), "Actual error message: " + e.getMessage());
      Assertions.assertEquals("http://ci.org/build/reports/tests/" + browser + "/1.jpg", e.getScreenshot());
    }
  }

  @Test
  void itShouldBeReportedIfParentCollectionIsNotFound() {
    try {
      $("#multirowTable")
        .findAll("thead")
        .findBy(text("mymail@gmail.com"))
        .find(".trash")
        .shouldBe(visible);
      fail();
    } catch (ElementNotFound e) {
      Assertions.assertTrue(
        e.getMessage().contains("Element not found {#multirowTable/thead"), "Actual error message: " + e.getMessage());
      Assertions.assertEquals("http://ci.org/build/reports/tests/" + browser + "/1.jpg", e.getScreenshot());
    }
  }

  @Test
  void elementNotFoundInsideParent() {
    try {
      $("#multirowTable")
        .findAll("tbody tr")
        .findBy(text("Norris"))
        .find(".second_row")
        .shouldBe(visible);
      fail();
    } catch (ElementNotFound e) {
      Assertions.assertTrue(
        e.getMessage().contains("Element not found {.second_row}"), "Actual error message: " + e.getMessage());
    }
  }

  @Test
  void elementShouldNotBeFoundAndParentAlsoNotFound() {
    $("#multirowTable")
      .find("theeeead")
      .find(".second_row")
      .shouldNotBe(visible);
  }
}
