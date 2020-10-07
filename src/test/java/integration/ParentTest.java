package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

final class ParentTest extends ITest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canGetImmediateParentElement() {
    assertThat($("#theHiddenElement").parent())
      .isEqualTo($("body"));
    assertThat($("h2").parent())
      .isEqualTo($("#domain-container"));
    assertThat($(By.name("domain")).parent())
      .isEqualTo($("#dropdown-list-container"));

    assertThat($("#multirowTableSecondRow"))
      .isEqualTo($(".second_row").parent());
  }

  @Test
  void canGetClosestMatchingAncestorByTagName() {
    assertThat($("#theHiddenElement").closest("body"))
      .isEqualTo($("body"));
    assertThat($("h2").closest("body"))
      .isEqualTo($("body"));
    assertThat($(By.name("domain")).closest("div"))
      .isEqualTo($("#dropdown-list-container"));
  }

  @Test
  void canGetClosestMatchingAncestorByClassName() {
    assertThat($(By.name("domain")).closest(".container"))
      .isEqualTo($("#dropdown-list-container"));

    assertThat($(".second_row").closest("tr"))
      .isEqualTo($("#multirowTableSecondRow"));
    assertThat($(".second_row").closest("table"))
      .isEqualTo($("#multirowTable"));
    assertThat($(".second_row").closest(".table"))
      .isEqualTo($("#multirowTable"));
    assertThat($(".second_row").closest(".multirow_table"))
      .isEqualTo($("#multirowTable"));
  }
}
