package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

class DynamicSelectsTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_dynamic_select.html");
  }

  @Test
  void waitsUntilOptionWithTextAppears() {
    $("#language").selectOption("l'a \"English\"");

    SelenideElement select = $("#language");
    select.getSelectedOption().shouldBe(selected);
    Assertions.assertEquals("'eng'", select.getSelectedValue());
    Assertions.assertEquals("l'a \"English\"", select.getSelectedText());
  }

  @Test
  void waitsUntilOptionWithValueAppears() {
    $("#language").selectOptionByValue("\"est\"");

    SelenideElement select = $("#language");
    select.getSelectedOption().shouldBe(selected);
    Assertions.assertEquals("\"est\"", select.getSelectedValue());
    Assertions.assertEquals("l'a \"Eesti\"", select.getSelectedText());
  }

  @Test
  void selectByXPath() {
    $(By.xpath("html/body/div[1]/form[1]/label[1]/select[1]")).selectOption("l'a \"English\"");
    Assertions.assertEquals("l'a \"English\"", $(By.xpath("html/body/div[1]/form[1]/label[1]/select[1]")).getSelectedText());
  }

  @Test
  void selectingOptionTriggersChangeEvent() {
    $("#language").selectOption("l'a \"English\"");
    $("h2").shouldHave(text("'eng'"));
  }

  @Test
  void selectingOptionRebuildsAnotherSelect() {
    $("#language").selectOption("l'a \"Русский\"");
    $("#books").selectOption("книжко");
  }
}
