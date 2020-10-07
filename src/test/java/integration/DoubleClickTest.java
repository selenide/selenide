package integration;

import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;

final class DoubleClickTest extends ITest {
  @Test
  void userCanDoubleClickOnElement() {
    openFile("page_with_double_clickable_button.html");

    $("#double-clickable-button")
      .shouldHave(value("double click me"))
      .shouldBe(enabled);

    $("#double-clickable-button")
      .doubleClick()
      .shouldHave(value("do not click me anymore"))
      .shouldBe(disabled);

    $("h2").shouldHave(text("Double click worked"));
  }
}
