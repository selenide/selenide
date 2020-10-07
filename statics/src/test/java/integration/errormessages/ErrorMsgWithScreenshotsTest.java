package integration.errormessages;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.Screenshots;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.impl.ScreenShotLaboratory;
import integration.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.NoSuchElementException;

import javax.annotation.Nonnull;
import java.io.File;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

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
    Configuration.reportsFolder = "build/reports/tests/ErrorMsgWithScreenshotsTest";
    Configuration.reportsUrl = "http://ci.org/";
    Screenshots.screenshots = new ScreenShotLaboratory() {
      @Override
      public String takeScreenShot(@Nonnull Driver driver) {
        return new File(reportsFolder, "1.jpg").getAbsolutePath();
      }
    };
  }

  @AfterEach
  void restoreScreenshots() {
    Configuration.reportsUrl = reportsUrl;
    Configuration.reportsFolder = reportsFolder;
    Screenshots.screenshots = ScreenShotLaboratory.getInstance();
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
      .hasMessageContaining("Element not found {#nonexisting-form/by text: mymail@gmail.com.findBy(css class 'trash')}")
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
      .hasMessageContaining("Element not found {thead}")
      .matches(e -> {
        String path = "/integration/errormessages/ErrorMsgWithScreenshotsTest/reportWhichParentElementIsNotFound";
        return ((ElementNotFound) e).getScreenshot()
          .matches("http://ci\\.org/build/reports/tests/ErrorMsgWithScreenshotsTest" + path + "/\\d+\\.\\d+\\.(png|html)");
      });
  }

  @Test
  void reportIfParentCollectionIsNotFound() {
    try {
      $("#multirowTable")
        .findAll("thead")
        .findBy(text("mymail@gmail.com"))
        .find(".trash")
        .shouldBe(visible);
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound e) {
      assertThat(e)
        .hasMessageContaining("Element not found {#multirowTable/thead");
      String path = "/integration/errormessages/ErrorMsgWithScreenshotsTest/reportIfParentCollectionIsNotFound";
      assertThat(e.getScreenshot())
        .matches("http://ci\\.org/build/reports/tests/ErrorMsgWithScreenshotsTest" + path + "/\\d+\\.\\d+\\.(png|html)");
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
      fail("Expected ElementNotFound");
    }
    catch (ElementNotFound e) {
      assertThat(e)
        .hasMessageContaining("Element not found {.second_row}");
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
