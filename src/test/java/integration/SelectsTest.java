package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.Assert.assertEquals;

public class SelectsTest extends IntegrationTest {
  @Before
  public void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  public void userCanSelectOptionByValue() {
    SelenideElement select = $(By.xpath("//select[@name='domain']"));
    select.selectOptionByValue("myrambler.ru");

    select.getSelectedOption().shouldBe(selected);
    assertEquals("myrambler.ru", select.getSelectedValue());
    assertEquals("@myrambler.ru", select.getSelectedText());
  }

  @Test
  public void valMethodSelectsOptionInCaseOfSelectBox() {
    SelenideElement select = $(By.xpath("//select[@name='domain']"));
    select.val("myrambler.ru");

    select.getSelectedOption().shouldBe(selected);
    assertEquals("myrambler.ru", select.getSelectedValue());
    assertEquals("@myrambler.ru", select.getSelectedText());
  }

  @Test
  public void userCanSelectOptionByText() {
    SelenideElement select = $(By.xpath("//select[@name='domain']"));
    select.selectOption("@мыло.ру");

    select.getSelectedOption().shouldBe(selected);
    assertEquals("мыло.ру", select.getSelectedValue());
    assertEquals("@мыло.ру", select.getSelectedText());
  }

  @Test
  public void optionValueWithApostrophe() {
    $("#hero").selectOptionByValue("john mc'lain");
    $("#hero").getSelectedOption().shouldHave(text("John Mc'Lain"));
  }

  @Test
  public void optionValueWithQuote() {
    $("#hero").selectOptionByValue("arnold \"schwarzenegger\"");
    $("#hero").getSelectedOption().shouldHave(text("Arnold \"Schwarzenegger\""));
  }

  @Test
  public void optionTextWithApostrophe() {
    $("#hero").selectOption("John Mc'Lain");
    $("#hero").getSelectedOption().shouldHave(value("john mc'lain"));
  }

  @Test
  public void optionTextWithQuote() {
    $("#hero").selectOption("Arnold \"Schwarzenegger\"");
    $("#hero").getSelectedOption().shouldHave(value("arnold \"schwarzenegger\""));
  }

  @Test
  public void optionTextWithApostropheInsideQuote() {
    $("#hero").selectOption("Mickey \"Rock'n'Roll\" Rourke");
    $("#hero").getSelectedOption().shouldHave(value("mickey rourke"));
  }

  @Test
  public void selectingOptionTriggersChangeEvent() {
    $("#selectedDomain").shouldBe(empty);
    
    $(By.xpath("//select[@name='domain']")).selectOption("@мыло.ру");
    $("#selectedDomain").shouldHave(text(("@мыло.ру")));
    
    $(By.xpath("//select[@name='domain']")).selectOptionByValue("myrambler.ru");
    $("#selectedDomain").shouldHave(text(("@myrambler.ru")));
  }
}
