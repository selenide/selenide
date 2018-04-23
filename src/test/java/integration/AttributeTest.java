package integration;

import org.junit.Before;
import org.junit.Test;

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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

public class AttributeTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void canVerifyAttributeExistence() {
    $("#domain-container").shouldHave(attribute("class"));
    $("#domain-container").shouldNotHave(attribute("foo"));
//    $("#domain-container").shouldNotHave(attribute("title")); // it's a bug in selenium webdriver
  }

  @Test
  public void canCheckAttributeExistence() {
    assertTrue($("#domain-container").has(attribute("class")));
    assertFalse($("#domain-container").has(attribute("foo")));
//    assertFalse($("#domain-container").has(attribute("title"))); // it's a bug in selenium webdriver
  }

  @Test
  public void userCanFindElementByAttribute() {
    assertEquals("select", $(byAttribute("name", "domain")).getTagName());
    assertEquals("@мыло.ру", $(byAttribute("value", "мыло.ру")).getText());
    assertEquals("div", $(byAttribute("id", "radioButtons")).getTagName());
    assertEquals(4, $$(byAttribute("type", "radio")).size());
    assertEquals("username", $(byAttribute("readonly", "readonly")).getAttribute("name"));
    assertEquals("meta", $(byAttribute("http-equiv", "Content-Type")).getTagName());
  }

  @Test
  public void userCanGetAttr() {
    assertEquals("username", $(by("readonly", "readonly")).attr("name"));
  }

  @Test
  public void userCanGetNameAttribute() {
    assertEquals("username", $(by("readonly", "readonly")).name());
  }

  @Test
  public void userCanGetDataAttributes() {
    assertEquals("111", $(byValue("livemail.ru")).getAttribute("data-mailServerId"));
    assertEquals("111", $(byValue("livemail.ru")).data("mailServerId"));

    assertEquals("222A", $(byText("@myrambler.ru")).data("mailServerId"));
    assertEquals("33333B", $(byValue("rusmail.ru")).data("mailServerId"));
    assertEquals("111АБВГД", $(byText("@мыло.ру")).data("mailServerId"));
  }


  @Test
  public void userCanSearchElementByDataAttribute() {
    assumeFalse(isChrome() || isHtmlUnit() || isPhantomjs());

    assertEquals("111", $(by("data-mailServerId", "111")).data("mailServerId"));
    assertEquals("222A", $(by("data-mailServerId", "222A")).data("mailServerId"));
    assertEquals("33333B", $(by("data-mailServerId", "33333B")).data("mailServerId"));
    assertEquals("111АБВГД", $(by("data-mailServerId", "111АБВГД")).data("mailServerId"));
  }

  @Test
  public void userCanSearchElementByTitleAttribute() {
    assertEquals("fieldset", $(byTitle("Login form")).getTagName());
  }
}
