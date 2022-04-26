package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byTextCaseInsensitive;
import static com.codeborne.selenide.Selectors.withTagAndText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selectors.withTextCaseInsensitive;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class ByTextTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void userCanFindElementByText() {
    $(byText("Page with selects")).shouldHave(text("Page with selects"));
    $(byText("Dropdown list")).shouldHave(text("Dropdown list"));
    $(byText("@livemail.ru")).shouldHave(text("@livemail.ru"));
  }

  @Test
  void userCanFindElementByTagAndText() {
    $(byTagAndText("h1", "Page with selects")).shouldHave(text("Page with selects"));
    $(byTagAndText("h2", "Page with selects")).shouldNot(exist);
    $(byTagAndText("h2", "Dropdown list")).shouldHave(text("Dropdown list"));
  }

  @Test
  void spacesInTextAreIgnored() {
    $(byText("L'a Baskerville")).shouldHave(text("L'a Baskerville"));
    $(withText("L'a Baskerville")).shouldHave(text("L'a Baskerville"));
    $(byTagAndText("td", "L'a Baskerville")).shouldHave(text("L'a Baskerville"));
    $(withTagAndText("td", "L'a Baskerville")).shouldHave(text("L'a Baskerville"));
    $(byTextCaseInsensitive("L'a BaskervILLE")).shouldHave(text("L'a Baskerville"));
    $(withTextCaseInsensitive("L'a BasKERVille")).shouldHave(text("L'a Baskerville"));
  }

  @Test
  void nonBreakableSpacesInTextAreIgnored() {
    $("#hello-world").shouldHave(text("Hello world"));
    $(byText("Hello world")).shouldHave(attribute("id", "hello-world"));
    $(withText("Hello world")).shouldHave(text("Hello world"));
    $(byTextCaseInsensitive("Hello WORLD")).shouldHave(attribute("id", "hello-world"));
    $(withTextCaseInsensitive("HELLO world")).shouldHave(text("Hello world"));
    $(byTagAndText("span", "Hello world")).shouldHave(attribute("id", "hello-world"));
    $(withTagAndText("span", "llo wor")).shouldHave(text("Hello world"));
    $(".level2").$(withText("Hello ")).shouldHave(text("Hello world"));
    $(".level2").$(withText(" world")).shouldHave(text("Hello world"));
  }

  @Test
  void canFindElementByTextInsideParentElement() {
    assertThat($("#multirowTable").findAll(byText("Chack")))
      .hasSize(2);
    assertThat($("#multirowTable tr").findAll(byText("Chack")))
      .hasSize(1);
    assertThat($("#multirowTable tr").find(byText("Chack")).getAttribute("class"))
      .isEqualTo("first_row");
  }

  @Test
  void canFindElementContainingText() {
    $(withText("age with s")).shouldHave(text("Page with selects"));
    $(withText("Dropdown")).shouldHave(text("Dropdown list"));
    $(withText("@livemail.r")).shouldHave(text("@livemail.ru"));
  }

  @Test
  void canFindElementWithTagContainingText() {
    $(withTagAndText("h1", "age with s")).shouldHave(text("Page with selects"));
    $(withTagAndText("h2", "Dropdown")).shouldHave(text("Dropdown list"));
    $(withTagAndText("option", "@livemail.r")).shouldHave(text("@livemail.ru"));
  }

  @Test
  void canFindElementContainingTextInsideParentElement() {
    $("#multirowTable").findAll(withText("Cha")).shouldHave(size(2));
    $("#multirowTable tr").findAll(withText("ack")).shouldHave(size(1));
    $("#multirowTable tr", 1).find(withText("hac")).shouldHave(cssClass("second_row"));
  }

  @Test
  void canFindElementsByI18nText() {
    $(byText("Маргарита")).shouldHave(attribute("id", "radioButtons"));
    $(withText("Марг")).shouldHave(attribute("id", "radioButtons"));
    $(byText("Кот \"Бегемот\"")).click();
    $(withText("т \"Бегемот\"")).click();
    $(byTextCaseInsensitive("КОТ \"бЕГЕмот\"")).click();
    $(withTextCaseInsensitive("т \"бегеМОТ\"")).click();
  }

  @Test
  void quotesInText() {
    $(byText("Arnold \"Schwarzenegger\"")).shouldBe(visible);
    $(byTagAndText("option", "Arnold \"Schwarzenegger\"")).shouldBe(visible);
    $(byTextCaseInsensitive("arnold \"schWARZenegger\"")).shouldBe(visible);
    $(withTextCaseInsensitive("old \"schWARZenegg")).shouldBe(visible);
    $("#hero").find(byText("Arnold \"Schwarzenegger\"")).shouldBe(visible);
    $("#hero").find(byTagAndText("option", "Arnold \"Schwarzenegger\"")).shouldBe(visible);
    $("#apostrophes-and-quotes").find(By.linkText("Options with 'apostrophes' and \"quotes\"")).click();
  }

  @Test
  void canFindByText_caseInsensitive() {
    $(byTextCaseInsensitive("PAGE with selects")).shouldHave(text("Page with selects"));
    $(byTextCaseInsensitive("PAGE with SELECTS")).shouldHave(text("Page with selects"));
    $$(byTextCaseInsensitive("PAGE with SELECTS")).shouldHave(size(1));

    assertThatThrownBy(() -> $(byTextCaseInsensitive("PAGE with SELECT")).should(exist))
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void canFindByText_inParent_caseInsensitive() {
    $(".level1").$(byTextCaseInsensitive("Hi WORLD")).shouldHave(text("Hi world"));
    $(".level2").$(byTextCaseInsensitive("Hello WORLD")).shouldHave(text("Hello world"), cssClass("hello2"));
    $(".level3").$(byTextCaseInsensitive("HELLO world")).shouldHave(text("Hello world"), cssClass("hello3"));
    $("#greetings").$$(byTextCaseInsensitive("HELLO world")).shouldHave(size(2));
  }

  @Test
  void canFindWithText_caseInsensitive() {
    $(withTextCaseInsensitive("Hello WORLD")).shouldHave(text("Hello world"));
    $(withTextCaseInsensitive("heLLO ")).shouldHave(text("Hello world"));
    $(withTextCaseInsensitive("O wORld")).shouldHave(text("Hello world"));
    $$(withTextCaseInsensitive("O wORld")).shouldHave(size(2));
    $(withTextCaseInsensitive("PAGE with SELECT")).should(exist);
    $$(withTextCaseInsensitive("PAGE with SELECT")).shouldHave(size(1));
  }

  @Test
  void canFindWithText_inParent_caseInsensitive() {
    $(".level1").$(withTextCaseInsensitive("i WOR")).shouldHave(text("Hi world"));
    $(".level2").$(withTextCaseInsensitive("llo WORLD")).shouldHave(text("Hello world"), cssClass("hello2"));
    $(".level3").$(withTextCaseInsensitive("ELLO worl")).shouldHave(text("Hello world"), cssClass("hello3"));
    $("#greetings").$$(withTextCaseInsensitive("ELLO worl")).shouldHave(size(2));
  }
}
