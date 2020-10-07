package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Condition.text;
import static org.assertj.core.api.Assertions.assertThat;

final class DynamicSelectsTest extends ITest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_dynamic_select.html");
    setTimeout(4000);
  }

  @Test
  void waitsUntilOptionWithTextAppears() {
    $("#language").selectOption("l'a \"English\"");

    SelenideElement select = $("#language");
    select.getSelectedOption().shouldBe(selected);
    assertThat(select.getSelectedValue())
      .isEqualTo("'eng'");
    assertThat(select.getSelectedText())
      .isEqualTo("l'a \"English\"");
  }

  @Test
  void waitsUntilOptionWithValueAppears() {
    $("#language").selectOptionByValue("\"est\"");

    SelenideElement select = $("#language");
    select.getSelectedOption().shouldBe(selected);
    assertThat(select.getSelectedValue())
      .isEqualTo("\"est\"");
    assertThat(select.getSelectedText())
      .isEqualTo("l'a \"Eesti\"");
  }

  @Test
  void selectByXPath() {
    $(By.xpath("html/body/div[1]/form[1]/label[1]/select[1]")).selectOption("l'a \"English\"");
    assertThat($(By.xpath("html/body/div[1]/form[1]/label[1]/select[1]")).getSelectedText())
      .isEqualTo("l'a \"English\"");
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
