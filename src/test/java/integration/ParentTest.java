package integration;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertEquals;

public class ParentTest extends IntegrationTest {
  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void canGetImmediateParentElement() {
    assertEquals($("body"), $("#theHiddenElement").parent());
    assertEquals($("#domain-container"), $("h2").parent());
    assertEquals($("#dropdown-list-container"), $(By.name("domain")).parent());

    assertEquals($("#multirowTableSecondRow"), $(".second_row").parent());
  }

  @Test
  public void canGetClosestMatchingAncestorByTagName() {
    assertEquals($("body"), $("#theHiddenElement").closest("body"));
    assertEquals($("body"), $("h2").closest("body"));
    assertEquals($("#dropdown-list-container"), $(By.name("domain")).closest("div"));
  }

  @Test
  public void canGetClosestMatchingAncestorByClassName() {
    assertEquals($("#dropdown-list-container"), $(By.name("domain")).closest(".container"));
    
    assertEquals($("#multirowTableSecondRow"), $(".second_row").closest("tr"));
    assertEquals($("#multirowTable"), $(".second_row").closest("table"));
    assertEquals($("#multirowTable"), $(".second_row").closest(".table"));
    assertEquals($("#multirowTable"), $(".second_row").closest(".multirow_table"));
  }
}
