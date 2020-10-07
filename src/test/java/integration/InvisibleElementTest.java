package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

final class InvisibleElementTest extends ITest {
  @BeforeEach
  void clickHidesElement() {
    openFile("elements_disappear_on_click.html");
    $("#hide").click();
    $("#hide").waitUntil(hidden, 2000);
  }

  @Test
  void shouldBeHidden() {
    $("#hide").shouldBe(hidden);
  }

  @Test
  void shouldNotBeVisible() {
    $("#hide").shouldNotBe(visible);
  }

  @Test
  void shouldNotHaveTextHide() {
    $("#hide").shouldNotHave(text("Hide me").because("Text should disappear"));
  }

  @Test
  void shouldHaveAttribute() {
    $("#hide").shouldHave(attribute("id", "hide").because("Attributes don't disappear"));
  }

  @Test
  void shouldHaveCssClasses() {
    $("#hide").shouldHave(cssClass("someclass").because("Attributes don't disappear"));
  }

  @Test
  void shouldNotHaveTextRemove() {
    $("#hide").shouldNotHave(text("Remove me").because("Text never existed."));
  }
}
