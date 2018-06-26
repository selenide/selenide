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
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.WebDriverRunner.isChrome;
import static com.codeborne.selenide.WebDriverRunner.isHtmlUnit;
import static com.codeborne.selenide.WebDriverRunner.isPhantomjs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AttributeTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canVerifyAttributeExistence() {
    $("#domain-container").shouldHave(attribute("class"));
    $("#domain-container").shouldNotHave(attribute("foo"));
//    $("#domain-container").shouldNotHave(attribute("title")); // it's a bug in selenium webdriver
  }

  @Test
  void canCheckAttributeExistence() {
    assertTrue($("#domain-container").has(attribute("class")));
    assertFalse($("#domain-container").has(attribute("foo")));
//    assertFalse($("#domain-container").has(attribute("title"))); // it's a bug in selenium webdriver
  }

  @Test
  void userCanFindElementByAttribute() {
    assertEquals("select", $(byAttribute("name", "domain")).getTagName());
    assertEquals("@мыло.ру", $(byAttribute("value", "мыло.ру")).getText());
    assertEquals("div", $(byAttribute("id", "radioButtons")).getTagName());
    assertEquals(4, $$(byAttribute("type", "radio")).size());
    assertEquals("username", $(byAttribute("readonly", "readonly")).getAttribute("name"));
    assertEquals("meta", $(byAttribute("http-equiv", "Content-Type")).getTagName());
  }

  @Test
  void userCanGetAttr() {
    assertEquals("username", $(by("readonly", "readonly")).attr("name"));
  }

  @Test
  void userCanGetNameAttribute() {
    assertEquals("username", $(by("readonly", "readonly")).name());
  }

  @Test
  void userCanGetDataAttributes() {
    assertEquals("111", $(byValue("livemail.ru")).getAttribute("data-mailServerId"));
    assertEquals("111", $(byValue("livemail.ru")).data("mailServerId"));

    assertEquals("222A", $(byText("@myrambler.ru")).data("mailServerId"));
    assertEquals("33333B", $(byValue("rusmail.ru")).data("mailServerId"));
    assertEquals("111АБВГД", $(byText("@мыло.ру")).data("mailServerId"));
  }

  @Test
  void userCanSearchElementByDataAttribute() {
    Assumptions.assumeFalse(isChrome() || isHtmlUnit() || isPhantomjs());

    assertEquals("111", $(by("data-mailServerId", "111")).data("mailServerId"));
    assertEquals("222A", $(by("data-mailServerId", "222A")).data("mailServerId"));
    assertEquals("33333B", $(by("data-mailServerId", "33333B")).data("mailServerId"));
    assertEquals("111АБВГД", $(by("data-mailServerId", "111АБВГД")).data("mailServerId"));
  }

  @Test
  void userCanSearchElementByTitleAttribute() {
    assertEquals("fieldset", $(byTitle("Login form")).getTagName());
  }
}
