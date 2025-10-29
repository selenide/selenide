package integration;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.href;
import static com.codeborne.selenide.Selectors.by;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byValue;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class AttributeTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canVerifyAttributeExistence() {
    $("#domain-container").shouldHave(attribute("class"));
    $("#domain-container").shouldNotHave(attribute("foo"));
  }

  @Test
  void canVerifyAttributeValue() {
    $("#domain-container").shouldHave(attribute("class", "container"));
    $("#domain-container").shouldNotHave(attribute("class", "kopli"));
  }

  @Test
  void canVerifyAttributeMatching() {
    $("#multirowTable").shouldHave(attributeMatching("class", ".*multirow_table.*"));
    $("#domain-container").shouldHave(attributeMatching("class", "contain.*"));
    $("#domain-container").shouldNotHave(attributeMatching("class", ".*another.*"));
    $("#domain-container").shouldNotHave(attributeMatching("foo", ".*contain.*"));
  }

  @Test
  void actual_value_attributeMatching() {
    assertThatThrownBy(() -> {
      $("#multirowTable").shouldHave(attributeMatching("class", ".*single_row_table.*"));
    })
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have match attribute class~/.*single_row_table.*/ {#multirowTable}")
      .hasMessageContaining("Element: '<table ")
      .hasMessageContaining("Actual value: class~/table multirow_table/");
  }

  @Test
  void canCheckAttributeExistence() {
    assertThat($("#domain-container").has(attribute("class")))
      .isTrue();
    assertThat($("#domain-container").has(attribute("foo")))
      .isFalse();
  }

  @Test
  void userCanFindElementByAttribute() {
    assertThat($(byAttribute("name", "domain")).getTagName())
      .isEqualTo("select");
    assertThat($(byAttribute("value", "four.ee")).getText())
      .isEqualTo("@four.ee");
    assertThat($(byAttribute("id", "radioButtons")).getTagName())
      .isEqualTo("div");
    assertThat($$(byAttribute("type", "radio")).size())
      .isEqualTo(4);
    assertThat($(byAttribute("readonly", "readonly")).getAttribute("name"))
      .isEqualTo("username");
    assertThat($(byAttribute("http-equiv", "Content-Type")).getTagName())
      .isEqualTo("meta");
  }

  @Test
  void userCanGetAttr() {
    SelenideElement element = $(by("readonly", "readonly"));
    assertThat(element.attr("name")).isEqualTo("username");
    assertThat(element.attr("value")).isEqualTo("");
    assertThat(element.attr("foo")).isNull();
  }

  @Test
  void userCanGetNameAttribute() {
    assertThat($(by("readonly", "readonly")).name()).isEqualTo("username");
    assertThat($("h2").name()).isNull();
  }

  @Test
  void userCanGetDataAttributes() {
    assertThat($(byValue("one.io")).getAttribute("data-mailServerId"))
      .isEqualTo("111");
    assertThat($(byValue("one.io")).data("mailServerId"))
      .isEqualTo("111");

    assertThat($(byText("@two.eu")).data("mailServerId"))
      .isEqualTo("222A");
    assertThat($(byValue("three.com")).data("mailServerId"))
      .isEqualTo("33333B");
    assertThat($(byText("@four.ee")).data("mailServerId"))
      .isEqualTo("111АБВГД");
  }

  @Test
  void userCanSearchElementByDataAttribute() {
    assertThat($(by("data-mailServerId", "111")).data("mailServerId"))
      .isEqualTo("111");
    assertThat($(by("data-mailServerId", "222A")).data("mailServerId"))
      .isEqualTo("222A");
    assertThat($(by("data-mailServerId", "33333B")).data("mailServerId"))
      .isEqualTo("33333B");
    assertThat($(by("data-mailServerId", "111АБВГД")).data("mailServerId"))
      .isEqualTo("111АБВГД");
  }

  @Test
  void canCheckHyperReference() {
    $("#non-clickable-element a").shouldHave(href("https://google.com"));
    $("#clickable-element a").shouldHave(href("https://duckduckgo.com"));
    $("#ajax-button").shouldHave(href("long_ajax_request.html"));
    $("#empty h3 a").shouldHave(href("#"));
  }
}
