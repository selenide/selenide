package integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;

class ParentTest extends IntegrationTest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void canGetImmediateParentElement() {
    Assertions.assertEquals($("body"), $("#theHiddenElement").parent());
    Assertions.assertEquals($("#domain-container"), $("h2").parent());
    Assertions.assertEquals($("#dropdown-list-container"), $(By.name("domain")).parent());

    Assertions.assertEquals($("#multirowTableSecondRow"), $(".second_row").parent());
  }

  @Test
  void canGetClosestMatchingAncestorByTagName() {
    Assertions.assertEquals($("body"), $("#theHiddenElement").closest("body"));
    Assertions.assertEquals($("body"), $("h2").closest("body"));
    Assertions.assertEquals($("#dropdown-list-container"), $(By.name("domain")).closest("div"));
  }

  @Test
  void canGetClosestMatchingAncestorByClassName() {
    Assertions.assertEquals($("#dropdown-list-container"), $(By.name("domain")).closest(".container"));

    Assertions.assertEquals($("#multirowTableSecondRow"), $(".second_row").closest("tr"));
    Assertions.assertEquals($("#multirowTable"), $(".second_row").closest("table"));
    Assertions.assertEquals($("#multirowTable"), $(".second_row").closest(".table"));
    Assertions.assertEquals($("#multirowTable"), $(".second_row").closest(".multirow_table"));
  }
}
