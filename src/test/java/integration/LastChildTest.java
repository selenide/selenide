package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.be;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class LastChildTest extends ITest {

  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canFindLastChildOnAGivenElement() {
    $x("//table[@id='user-table']/thead/tr").lastChild().shouldHave(text("Age"));
  }

  @Test
  void chainingLastChildFunctionsCorrectly() {
    $("#multirowTable").lastChild().lastChild().shouldHave(attribute("id", "multirowTableSecondRow"));
  }

  @Test
  void throwsExceptionWhenNoChildrenExist() {
    assertThatThrownBy(() -> $x("//span[@id='hello-world']").lastChild().should(be(visible)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith(String.format("Element not found {By.xpath: *[last()]}%nExpected: be visible"));
  }

}
