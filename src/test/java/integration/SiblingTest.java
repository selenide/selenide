package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.id;
import static com.codeborne.selenide.Condition.text;
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
  void errorWhenSiblingAbsent() {
    assertThatThrownBy(() -> $("#multirowTableFirstRow").sibling(3).click())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {By.xpath: following-sibling::*[4]}");
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
    assertThatThrownBy(() -> $("#multirowTableSecondRow").preceding(3).click())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {By.xpath: preceding-sibling::*[4]}");
  }
}
