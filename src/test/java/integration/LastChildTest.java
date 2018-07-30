package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class LastChildTest extends IntegrationTest {

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
      .hasMessage("Element not found {By.xpath: *[last()]}\nExpected: be visible");
  }

}
