package integration;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selectors.by;
import static com.codeborne.selenide.Selectors.byAttribute;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byTitle;
import static com.codeborne.selenide.Selectors.byValue;

public class AttributeTest extends ITest {
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
    assertThat($(byAttribute("value", "мыло.ру")).getText())
      .isEqualTo("@мыло.ру");
    assertThat($(byAttribute("id", "radioButtons")).getTagName())
      .isEqualTo("div");
    assertThat($$(byAttribute("type", "radio")))
      .hasSize(4);
    assertThat($(byAttribute("readonly", "readonly")).getAttribute("name"))
      .isEqualTo("username");
    assertThat($(byAttribute("http-equiv", "Content-Type")).getTagName())
      .isEqualTo("meta");
  }

  @Test
  void userCanGetAttr() {
    assertThat($(by("readonly", "readonly")).attr("name"))
      .isEqualTo("username");
  }

  @Test
  void userCanGetNameAttribute() {
    assertThat($(by("readonly", "readonly")).name())
      .isEqualTo("username");
  }

  @Test
  void userCanGetDataAttributes() {
    assertThat($(byValue("livemail.ru")).getAttribute("data-mailServerId"))
      .isEqualTo("111");
    assertThat($(byValue("livemail.ru")).data("mailServerId"))
      .isEqualTo("111");

    assertThat($(byText("@myrambler.ru")).data("mailServerId"))
      .isEqualTo("222A");
    assertThat($(byValue("rusmail.ru")).data("mailServerId"))
      .isEqualTo("33333B");
    assertThat($(byText("@мыло.ру")).data("mailServerId"))
      .isEqualTo("111АБВГД");
  }

  @Test
  void userCanSearchElementByDataAttribute() {
    Assumptions.assumeFalse(browser().isChrome() || browser().isHtmlUnit() || browser().isPhantomjs());

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
  void userCanSearchElementByTitleAttribute() {
    assertThat($(byTitle("Login form")).getTagName())
      .isEqualTo("fieldset");
  }
}
