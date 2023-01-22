package integration;

import com.codeborne.selenide.ClickOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;

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

      $("h2").shouldHave(text("Double click worked"));
    });
  }

  @Test
  void userCanDoubleClickOnElementWithJs() {
    withLongTimeout(() -> {
      $("#double-clickable-button")
        .shouldHave(value("double click me"))
        .shouldBe(enabled);

      $("#double-clickable-button")
        .doubleClick(ClickOptions.usingJavaScript())
        .shouldHave(value("do not click me anymore"))
        .shouldBe(disabled);

      $("h2").shouldHave(text("Double click worked"));
    });
  }

  @Test
  void userCanDoubleClickOnElementWithDefaultClickOption() {
    withLongTimeout(() -> {
      $("#double-clickable-button")
        .shouldHave(value("double click me"))
        .shouldBe(enabled);

      $("#double-clickable-button")
        .doubleClick(ClickOptions.usingDefaultMethod())
        .shouldHave(value("do not click me anymore"))
        .shouldBe(disabled);

      $("h2").shouldHave(text("Double click worked"));
    });
  }
}
