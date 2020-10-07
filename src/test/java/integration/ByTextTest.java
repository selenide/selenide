package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static org.assertj.core.api.Assertions.assertThat;

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
  void spacesInTextAreIgnored() {
    $(byText("L'a Baskerville")).shouldHave(text("L'a Baskerville"));
    $(withText("L'a Baskerville")).shouldHave(text("L'a Baskerville"));
  }

  @Test
  void nonBreakableSpacesInTextAreIgnored() {
    $("#hello-world").shouldHave(text("Hello world"));
    $(byText("Hello world")).shouldHave(attribute("id", "hello-world"));
    $(withText("Hello world")).shouldHave(text("Hello world"));
    $(withText("Hello ")).shouldHave(text("Hello world"));
    $(withText(" world")).shouldHave(text("Hello world"));
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
  void canFindElementContainingTextInsideParentElement() {
    $("#multirowTable").findAll(withText("Cha")).shouldHave(size(2));
    $("#multirowTable tr").findAll(withText("ack")).shouldHave(size(1));
    $("#multirowTable tr", 1).find(withText("hac")).shouldHave(cssClass("second_row"));
  }

  @Test
  void canFindElementsByI18nText() {
    $(byText("Маргарита")).shouldHave(text("Маргарита"));
    $(withText("Марг")).shouldHave(text("Маргарита"));
    $(byText("Кот \"Бегемот\"")).click();
  }

  @Test
  void quotesInText() {
    $(byText("Arnold \"Schwarzenegger\"")).shouldBe(visible);
    $("#hero").find(byText("Arnold \"Schwarzenegger\"")).shouldBe(visible);
    $("#apostrophes-and-quotes").find(By.linkText("Options with 'apostrophes' and \"quotes\"")).click();
  }
}
