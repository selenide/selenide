package integration;

import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.be;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class LastChildTest extends IntegrationTest {

  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canFindLastChildOnAGivenElement() {
    assertThat($x("//table[@id='user-table']/thead/tr").lastChild().text())
      .isEqualTo("Age");

  }

  @Test
  void throwsExceptionWhenNoChildrenExist() {
    assertThatThrownBy(() -> $x("//span[@id='hello-world']").lastChild().should(be(visible)))
      .isInstanceOf(ElementNotFound.class)
      .hasMessage("Element not found {By.xpath: *[last()]}\nExpected: be visible");
  }

  @Test
  void chainingLastChildFunctionsCorrectly() {
    assertThat($("#multirowTable").lastChild().lastChild().getAttribute("id"))
      .isEqualTo("multirowTableSecondRow");
  }

}
