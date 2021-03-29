package integration;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class SiblingTest extends ITest {
  @BeforeEach
  void openTestPageWith() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canGetSiblingElement() {
    $("#multirowTableFirstRow").sibling(0).shouldHave(id("multirowTableSecondRow"));
    $(".first_row").sibling(0).shouldHave(text("Norris"));
  }

  @Test
  void canGetSiblingOfParent() {
    $(".first_row").parent().sibling(0).find("td", 1).shouldHave(id("baskerville"));
  }

  @Test
  void canGetSiblingByTag() {
    $("#text-area").sibling("input").shouldHave(value("18"));
  }

  @Test
  void errorWhenSiblingByTagAbsent() {
    SelenideElement element = $("#text-area").sibling("div");
    assertThatThrownBy(element::click)
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#text-area/By.xpath: following-sibling::div}");
  }

  @Test
  void canGetSiblingByTagAndIndex() {
    $("#text-area").sibling("input", 1).shouldHave(value("Log in"));
  }

  @Test
  void errorWhenSiblingByTagAndIndexAbsent() {
    SelenideElement element = $("#text-area").sibling("div", 0);
    assertThatThrownBy(element::click)
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#text-area/By.xpath: following-sibling::div[1]}");
  }

  @Test
  void canGetSiblingByTagAndAttribute() {
    $("#text-area").sibling("input", "type", "submit").shouldHave(value("Log in"));
  }

  @Test
  void errorWhenSiblingByTagAndAttributeAbsent() {
    SelenideElement element = $("#text-area").sibling("input", "name", "qwe");
    assertThatThrownBy(element::click)
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#text-area/By.xpath: following-sibling::input[@name='qwe']}");
  }

  @Test
  void errorWhenSiblingAbsent() {
    SelenideElement element = $("#multirowTableFirstRow").sibling(3);
    assertThatThrownBy(element::click)
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#multirowTableFirstRow/By.xpath: following-sibling::*[4]}");
  }

  @Test
  void canGetPrecedingElement() {
    $("#multirowTableSecondRow").preceding(0).shouldHave(id("multirowTableFirstRow"));
    $("#baskerville").preceding(0).shouldHave(text("Chack"));
  }

  @Test
  void canGetPrecedingElementOfParent() {
    $(".second_row").parent().preceding(0).find("td", 0).shouldHave(cssClass("first_row"));
  }

  @Test
  void errorWhenPrecedingElementAbsent() {
    SelenideElement element = $("#multirowTableSecondRow").preceding(3);
    assertThatThrownBy(element::click)
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {#multirowTableSecondRow/By.xpath: preceding-sibling::*[4]}");
  }
}
