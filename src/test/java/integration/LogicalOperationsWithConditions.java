package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShouldNot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.and;
import static com.codeborne.selenide.Condition.be;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LogicalOperationsWithConditions extends ITest {
  @BeforeEach
  void openPage() {
    openFile("elements_disappear_on_click.html");
  }

  @Test
  void not_be() {
    $(".lolkek").shouldNotBe(visible);
    $(".lolkek").shouldBe(not(visible));
    $(".lolkek").shouldNot(be(visible));
    $(".lolkek").shouldNot(be(not(not(visible))));
    $(".lolkek").shouldNot(not(be(not(visible))));
  }

  @Test
  void andRevertsMissingElementTolerance() {
    $(".lolkek").shouldNotBe(and("visible&visible", visible, visible));

    assertThatThrownBy(() ->
      $(".lolkek").shouldNotHave(text("Lasnamäe")))
      .isInstanceOf(ElementNotFound.class);
    assertThatThrownBy(() ->
      $(".lolkek").shouldNotBe(and("visible&text", visible, text("Lasnamäe"))))
      .isInstanceOf(ElementNotFound.class);
    assertThatThrownBy(() ->
      $(".lolkek").shouldNotBe(and("visible&text", visible, text("Lasnamäe")), ofMillis(5)))
      .isInstanceOf(ElementNotFound.class);
    assertThatThrownBy(() ->
      $(".lolkek").shouldNotBe(and("text&visible", text("Lasnamäe"), visible)))
      .isInstanceOf(ElementNotFound.class);
    assertThatThrownBy(() ->
      $(".lolkek").shouldNotBe(and("text&visible", text("Lasnamäe"), visible), ofMillis(5)))
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void orRevertsMissingElementTolerance() {
    $(".lolkek").shouldNotBe(or("visible||exist", visible, exist));
    assertThatThrownBy(() -> $(".lolkek").shouldNotBe(or("visible||text", visible, text("Lasnamäe"))))
      .isInstanceOf(ElementNotFound.class);
    assertThatThrownBy(() -> $(".lolkek").shouldNotBe(or("text||visible", text("Lasnamäe"), visible)))
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void andRevertsExistingElement() {
    $("h1").shouldHave(text("Element removed"), text("from DOM"));
    $("h1").shouldHave(and("2 texts", text("Element removed"), text("from DOM")));
    $("h1").shouldNotHave(and("2 texts", text("Lasnamäe"), text("from DOM")));
    $("h1").shouldNotHave(and("2 texts", text("Element removed"), text("Lasnamäe")));

    assertThatThrownBy(() -> $("h1").shouldNotHave(and("2 texts", text("Element removed"), text("from DOM"))))
      .isInstanceOf(ElementShouldNot.class);
  }

  @Test
  void orRevertsExistingElement() {
    String text1 = "Element removed";
    String text2 = "from DOM";
    $("h1").shouldHave(or("2 texts", text(text1), text(text2)));
    $("h1").shouldHave(or("2 texts", text("Lasnamäe"), text(text2)));
    $("h1").shouldHave(or("2 texts", text(text1), text("Lasnamäe")));
    $("h1").shouldNotHave(or("2 texts", text("Tallinn"), text("Lasnamäe")));

    assertThatThrownBy(() ->
      $("h1").shouldNotHave(or("2 texts", text(text1), text(text2))))
      .isInstanceOf(ElementShouldNot.class);
    assertThatThrownBy(() ->
      $("h1").shouldNotHave(or("2 texts", text("Lasnamäe"), text(text2))))
      .isInstanceOf(ElementShouldNot.class);
    assertThatThrownBy(() ->
      $("h1").shouldNotHave(or("2 texts", text(text1), text("Lasnamäe"))))
      .isInstanceOf(ElementShouldNot.class);
  }
}
