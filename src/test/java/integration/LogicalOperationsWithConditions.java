package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShouldNot;
import com.codeborne.selenide.ex.UIAssertionError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Condition.and;
import static com.codeborne.selenide.Condition.be;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.or;
import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class LogicalOperationsWithConditions extends ITest {
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
    $(".lolkek").shouldNotBe(or("visible||text", visible, text("Lasnamäe")));
    $(".lolkek").shouldNotBe(or("text||visible", text("Lasnamäe"), visible));
  }

  @Test
  void or_newerSyntax() {
    $("#remove").shouldBe(visible.or(exist));
    $("#remove").shouldBe(text("Remove me").or(text("Delete me")));
    $("#remove").shouldBe(text("Delete me").or(text("Remove me")));
  }

  @Test
  void or_newerSyntax_negative() {
    $(".lolkek").shouldNotBe(visible.or(exist));
    $(".lolkek").shouldNotBe(visible.or(text("Lasnamäe")));
    $(".lolkek").shouldNotBe(text("Lasnamäe").or(visible));
  }

  @Test
  void or_newerSyntax_errorMessage() {
    assertThatThrownBy(() -> $("#remove").shouldBe(text("Add me").or(text("Update me")), Duration.ofMillis(3)))
      .isInstanceOf(UIAssertionError.class)
      .hasMessageStartingWith("Element should be text \"Add me\" OR text \"Update me\" {#remove}")
      .hasMessageContaining("Actual value: text=\"Remove me\"")
      .hasMessageContaining("Screenshot: ")
      .hasMessageContaining("Timeout: 3 ms.");
  }

  @Test
  void andRevertsExistingElement() {
    $("h1").shouldHave(partialText("Element removed"), partialText("from DOM"));
    $("h1").shouldHave(and("2 texts", partialText("Element removed"), partialText("from DOM")));
    $("h1").shouldNotHave(and("2 texts", partialText("Lasnamäe"), partialText("from DOM")));
    $("h1").shouldNotHave(and("2 texts", partialText("Element removed"), partialText("Lasnamäe")));

    assertThatThrownBy(() -> $("h1").shouldNotHave(and("2 texts", partialText("Element removed"), partialText("from DOM"))))
      .isInstanceOf(ElementShouldNot.class);
  }

  @Test
  void orRevertsExistingElement() {
    String text1 = "Element removed";
    String text2 = "from DOM";
    $("h1").shouldHave(or("2 texts", partialText(text1), partialText(text2)));
    $("h1").shouldHave(or("2 texts", partialText("Lasnamäe"), partialText(text2)));
    $("h1").shouldHave(or("2 texts", partialText(text1), partialText("Lasnamäe")));
    $("h1").shouldNotHave(or("2 texts", partialText("Tallinn"), partialText("Lasnamäe")));

    assertThatThrownBy(() ->
      $("h1").shouldNotHave(or("2 texts", partialText(text1), partialText(text2))))
      .isInstanceOf(ElementShouldNot.class);
    assertThatThrownBy(() ->
      $("h1").shouldNotHave(or("2 texts", partialText("Lasnamäe"), partialText(text2))))
      .isInstanceOf(ElementShouldNot.class);
    assertThatThrownBy(() ->
      $("h1").shouldNotHave(or("2 texts", partialText(text1), partialText("Lasnamäe"))))
      .isInstanceOf(ElementShouldNot.class);
  }
}
