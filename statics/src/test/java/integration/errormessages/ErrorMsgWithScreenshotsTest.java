package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.ElementNotFound;
import integration.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static integration.errormessages.Helper.getReportsFolder;
import static integration.errormessages.Helper.screenshot;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ErrorMsgWithScreenshotsTest extends IntegrationTest {
  private String reportsUrl;
  private String reportsFolder;

  @BeforeEach
  void setTimeout() {
    timeout = 0;
    openFile("page_with_selects_without_jquery.html");
  }

  @BeforeEach
  void mockScreenshots() {
    reportsUrl = Configuration.reportsUrl;
    reportsFolder = Configuration.reportsFolder;
    Configuration.reportsFolder = getReportsFolder();
    Configuration.reportsUrl = "http://ci.org/";
  }

  @AfterEach
  void restoreScreenshots() {
    Configuration.reportsUrl = reportsUrl;
    Configuration.reportsFolder = reportsFolder;
  }

  @Test
  void parentNotFound() {
    assertThatThrownBy(() ->
      $("#nonexisting-form")
        .findAll(byText("mymail@gmail.com"))
        .find(cssClass("trash"))
        .shouldBe(visible)
    )
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {#nonexisting-form/by text: mymail@gmail.com.findBy(css class \"trash\")}")
      .getCause()
      .isInstanceOf(NoSuchElementException.class)
      .hasMessageContainingAll("Unable to locate element", "#nonexisting-form");
  }

  @Test
  void reportWhichParentElementIsNotFound() {
    assertThatThrownBy(() ->
      $("#multirowTable")
        .find("thead")
        .find(byText("mymail@gmail.com"))
        .find(".trash")
        .shouldBe(visible)
    )
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#multirowTable/thead}")
      .has(screenshot(path("reportWhichParentElementIsNotFound")));
  }

  @Test
  void reportIfParentCollectionIsNotFound() {
    assertThatThrownBy(() -> $("#multirowTable")
      .findAll("thead")
      .findBy(text("mymail@gmail.com"))
      .find(".trash")
      .shouldBe(visible)
    )
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {#multirowTable/thead")
      .has(screenshot(path("reportIfParentCollectionIsNotFound")));
  }

  @Test
  void elementNotFoundInsideParent() {
    assertThatThrownBy(() -> $("#multirowTable")
      .findAll("tbody tr")
      .findBy(partialText("Norris"))
      .find(".second_row")
      .shouldBe(visible)
    )
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#multirowTable/tbody tr.findBy(partial text \"Norris\")/.second_row}");
  }

  @Test
  void elementShouldNotBeFoundAndParentAlsoNotFound() {
    $("#multirowTable")
      .find("theeeead")
      .find(".second_row")
      .shouldNotBe(visible);
  }

  private static String path(String testName) {
    return Helper.path("ErrorMsgWithScreenshotsTest", testName);
  }
}
