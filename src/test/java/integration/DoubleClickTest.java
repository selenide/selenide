package integration;

import org.junit.jupiter.api.RepeatedTest;

import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;

final class DoubleClickTest extends ITest {
  @RepeatedTest(100)
  void userCanDoubleClickOnElement() throws InterruptedException {
    if (driver().browser().isChrome()) {
      Thread.sleep(500);
    }
    openFile("page_with_double_clickable_button.html");

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
}
