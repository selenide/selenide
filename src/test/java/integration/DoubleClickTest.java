package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.ClickOptions.usingDefaultMethod;
import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static integration.Coordinates.coordinates;

/**
 * button size is 200x20  -> center point is at 100x10
 * area is 800x400        -> center point is at 400x200
 */
final class DoubleClickTest extends ITest {

  @BeforeEach
  public void prepare() {
    openFile("page_with_double_clickable_button.html");
  }

  @Test
  void userCanDoubleClickOnElement() {
    withLongTimeout(() -> {
      $("#double-clickable-button")
        .shouldHave(value("double click me"))
        .shouldBe(enabled);

      $("#double-clickable-button")
        .doubleClick()
        .shouldHave(value("do not click me anymore"))
        .shouldBe(disabled);

      $("h2").shouldHave(text("Status: double-clicked the button"));
      $("#coords").shouldHave(coordinates(100, 10));
    });
  }

  @Test
  void userCanDoubleClickOnElementWithJs() {
    withLongTimeout(() -> {
      $("#double-clickable-button")
        .shouldHave(value("double click me"))
        .shouldBe(enabled);

      $("#double-clickable-button")
        .doubleClick(usingJavaScript())
        .shouldHave(value("do not click me anymore"))
        .shouldBe(disabled);

      $("h2").shouldHave(text("Status: double-clicked the button"));
      $("#coords").shouldHave(coordinates(100, 10));
    });
  }

  @Test
  void userCanDoubleClickOnElementWithDefaultClickOption() {
    withLongTimeout(() -> {
      $("#double-clickable-button")
        .shouldHave(value("double click me"))
        .shouldBe(enabled);

      $("#double-clickable-button")
        .doubleClick(usingDefaultMethod())
        .shouldHave(value("do not click me anymore"))
        .shouldBe(disabled);

      $("h2").shouldHave(text("Status: double-clicked the button"));
      $("#coords").shouldHave(coordinates(100, 10));
    });
  }

  @Test
  void userCanDoubleClickElement() {
    $("#double-clickable-area").doubleClick(usingDefaultMethod());
    $("h2").shouldHave(text("Status: double-clicked the area"));
    $("#coords").shouldHave(coordinates(400, 200));
  }

  @Test
  void userCanDoubleClickElement_usingJavaScript() {
    $("#double-clickable-area").doubleClick(usingJavaScript());
    $("h2").shouldHave(text("Status: double-clicked the area"));
    $("#coords").shouldHave(coordinates(400, 200));
  }

  @Test
  void userCanDoubleClickElement_withOffset() {
    $("#double-clickable-area").doubleClick(usingDefaultMethod().offset(66, 33));
    $("h2").shouldHave(text("Status: double-clicked the area"));
    $("#coords").shouldHave(coordinates(466, 233));
  }

  @Test
  void userCanDoubleClickElement_withOffset_usingJavaScript() {
    $("#double-clickable-area").doubleClick(usingJavaScript().offset(66, 33));
    $("h2").shouldHave(text("Status: double-clicked the area"));
    $("#coords").shouldHave(coordinates(466, 233));
  }
}
