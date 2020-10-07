package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.disappears;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;

/**
 * All checks in this class are equivalent
 */
final class ElementRemovedTest extends ITest {
  @BeforeEach
  void clickRemovesElement() {
    openFile("elements_disappear_on_click.html");
    setTimeout(2000);
    $("#remove").click();
  }

  @Test
  void shouldBeHidden() {
    $("#remove").shouldBe(hidden);
  }

  @Test
  void shouldDisappear() {
    $("#remove").should(disappear);
  }

  @Test
  void waitUntilDisappears() {
    $("#remove").waitUntil(disappears, 2000);
  }

  @Test
  void shouldNotBeVisible() {
    $("#remove").shouldNotBe(visible);
  }

  @Test
  void shouldNotExist() {
    $("#remove").shouldNot(exist);
  }

  @Test
  void shouldNotAppear() {
    $("#remove").shouldNot(appear);
  }
}
