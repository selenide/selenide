package integration;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SiblingTest extends ITest {
  @BeforeEach
  void openTestPageWith() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canGetSiblingElement() {
    assertThat($("#multirowTableFirstRow").sibling(0))
      .isEqualTo($("#multirowTableSecondRow"));
    $(".first_row").sibling(0).has(Condition.text("Norris"));
  }

  @Test
  void canGetSiblingOfParent() {
    assertThat($(".first_row").parent().sibling(0).find("td", 1))
      .isEqualTo($("#baskerville"));
  }

  @Test
  void errorWhenSiblingAbsent() {
    assertThatThrownBy(() -> $("#multirowTableFirstRow").sibling(3).click())
      .isInstanceOf(ElementNotFound.class);
  }

}
