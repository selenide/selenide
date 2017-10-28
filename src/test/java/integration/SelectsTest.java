package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class SelectsTest extends IntegrationTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @After
  public void resetProperties() {
    Configuration.versatileSetValue = false;
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
  public void userCanSelectValueUsingSetValue() {
    Configuration.versatileSetValue = true;
    SelenideElement select = $(byName("domain"));
    select.setValue("myrambler.ru");

    assertEquals("myrambler.ru", select.getSelectedValue());
    assertEquals("@myrambler.ru", select.getSelectedText());
  }

  @Test
  public void userCanSelectOptionByIndex() {
    SelenideElement select = $(By.xpath("//select[@name='domain']"));

    select.selectOption(0);
    assertThat(select.getSelectedText(), equalTo("@livemail.ru"));

    select.selectOption(1);
    assertThat(select.getSelectedText(), equalTo("@myrambler.ru"));

    select.selectOption(2);
    assertThat(select.getSelectedText(), equalTo("@rusmail.ru"));
    
    select.selectOption(3);
    assertThat(select.getSelectedText(), equalTo("@мыло.ру"));
  }

  @Test()
  public void throwsElementNotFoundWithOptionsText() {
    thrown.expect(ElementNotFound.class);
    thrown.expectMessage("Element not found {By.xpath: //select[@name='domain']/option[text:unexisting-option]}\n"
        + "Expected: exist");
    $x("//select[@name='domain']").selectOption("unexisting-option");
  }

  @Test()
  public void throwsElementNotFoundWithOptionsIndex() {
    thrown.expect(ElementNotFound.class);
    thrown.expectMessage("Element not found {By.xpath: //select[@name='domain']/option[index:999]}\n"
        + "Expected: exist");
    $x("//select[@name='domain']").selectOption(999);
  }

  @Test
  public void valMethodSelectsOptionInCaseOfSelectBox() {
    Configuration.versatileSetValue = true;
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
  public void userCanSelectOptionByPartialText() {
    SelenideElement select = $(By.xpath("//select[@name='domain']"));
    select.selectOptionContainingText("ыло.р");

    assertEquals("@мыло.ру", select.getSelectedText());
  }

  @Test
  public void getTextReturnsTextsOfSelectedOptions() {
    assertEquals("-- Select your hero --", $("#hero").getText());

    $("#hero").selectOptionByValue("john mc'lain");
    assertEquals("John Mc'Lain", $("#hero").getText());
  }

  @Test
  public void shouldHaveTextChecksSelectedOption() {
    $("#hero").shouldNotHave(text("John Mc'Lain").because("Option is not selected yet"));

    $("#hero").selectOptionByValue("john mc'lain");
    $("#hero").shouldHave(text("John Mc'Lain").because("Option `john mc'lain` is selected"));
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
    $("#selectedDomain").shouldHave(text("@мыло.ру"));
    
    $(By.xpath("//select[@name='domain']")).selectOptionByValue("myrambler.ru");
    $("#selectedDomain").shouldHave(text("@myrambler.ru"));
  }
}
