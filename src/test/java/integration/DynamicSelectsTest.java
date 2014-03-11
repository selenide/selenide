package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertEquals;

public class DynamicSelectsTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("page_with_dynamic_select.html");
  }

  @Test
  public void waitsUntilOptionWithTextAppears() {
    $("#language").selectOption("l'a \"English\"");

    SelenideElement select = $("#language");
    select.getSelectedOption().shouldBe(selected);
    assertEquals("'eng'", select.getSelectedValue());
    assertEquals("l'a \"English\"", select.getSelectedText());
  }

  @Test
  public void waitsUntilOptionWithValueAppears() {
    $("#language").selectOptionByValue("\"est\"");

    SelenideElement select = $("#language");
    select.getSelectedOption().shouldBe(selected);
    assertEquals("\"est\"", select.getSelectedValue());
    assertEquals("l'a \"Eesti\"", select.getSelectedText());
  }
}
