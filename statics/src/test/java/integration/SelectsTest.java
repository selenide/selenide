package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class SelectsTest extends IntegrationTest {

  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void userCanSelectOptionByValue() {
    SelenideElement select = $(By.xpath("//select[@name='domain']"));
    select.selectOptionByValue("myrambler.ru");

    select.getSelectedOption().shouldBe(selected);
    assertThat(select.getSelectedValue())
      .isEqualTo("myrambler.ru");
    assertThat(select.getSelectedText())
      .isEqualTo("@myrambler.ru");
  }

  @Test
  void selectOptionByValue_errorMessage() {
    assertThatThrownBy(() -> {
      SelenideElement select = $(By.xpath("//select[@name='domain']"));
      select.selectOptionByValue("wrong-value");
    })
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {By.xpath: //select[@name='domain']/option[value:wrong-value]}");
  }

  @Test
  void selectOptionByText_errorMessage() {
    assertThatThrownBy(() -> {
      SelenideElement select = $(By.xpath("//select[@name='domain']"));
      select.selectOption("wrong-text");
    })
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {By.xpath: //select[@name='domain']/option[text:wrong-text]}");
  }

  @Test
  void userCanSelectValueUsingSetValue() {
    Configuration.versatileSetValue = true;
    SelenideElement select = $(byName("domain"));
    select.setValue("myrambler.ru");

    assertThat(select.getSelectedValue())
      .isEqualTo("myrambler.ru");
    assertThat(select.getSelectedText())
      .isEqualTo("@myrambler.ru");
  }

  @Test
  void userCanSelectOptionByIndex() {
    SelenideElement select = $(By.xpath("//select[@name='domain']"));

    select.selectOption(0);
    assertThat(select.getSelectedText())
      .isEqualTo("@livemail.ru");

    select.selectOption(1);
    assertThat(select.getSelectedText())
      .isEqualTo("@myrambler.ru");

    select.selectOption(2);
    assertThat(select.getSelectedText())
      .isEqualTo("@rusmail.ru");

    select.selectOption(3);
    assertThat(select.getSelectedText())
      .isEqualTo("@мыло.ру");
  }

  @Test()
  void throwsElementNotFoundWithOptionsText() {
    assertThatThrownBy(() -> $x("//select[@name='domain']").selectOption("unexisting-option"))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith(
        String.format("Element not found {By.xpath: //select[@name='domain']/option[text:unexisting-option]}%nExpected: exist"));
  }

  @Test()
  void throwsElementNotFoundWithOptionsIndex() {
    assertThatThrownBy(() -> $x("//select[@name='domain']").selectOption(999))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith(
        String.format("Element not found {By.xpath: //select[@name='domain']/option[index:999]}%nExpected: exist"));
  }

  @Test
  void valMethodSelectsOptionInCaseOfSelectBox() {
    Configuration.versatileSetValue = true;
    SelenideElement select = $(By.xpath("//select[@name='domain']"));
    select.val("myrambler.ru");

    select.getSelectedOption().shouldBe(selected);
    assertThat(select.getSelectedValue())
      .isEqualTo("myrambler.ru");
    assertThat(select.getSelectedText())
      .isEqualTo("@myrambler.ru");
  }

  @Test
  void userCanSelectOptionByText() {
    SelenideElement select = $(By.xpath("//select[@name='domain']"));
    select.selectOption("@мыло.ру");

    select.getSelectedOption().shouldBe(selected);
    assertThat(select.getSelectedValue())
      .isEqualTo("мыло.ру");
    assertThat(select.getSelectedText())
      .isEqualTo("@мыло.ру");
  }

  @Test
  void userCanSelectOptionByPartialText() {
    SelenideElement select = $(By.xpath("//select[@name='domain']"));
    select.selectOptionContainingText("ыло.р");

    assertThat(select.getSelectedText())
      .isEqualTo("@мыло.ру");
  }

  @Test
  void getSelectedText_cannotBeNull() {
    SelenideElement select = $("select#gender");
    assertThat(select.getSelectedText()).isEqualTo("");

    select.selectOptionContainingText("emal");
    assertThat(select.getSelectedText()).isEqualTo("Female");

    assertThatThrownBy(() -> $("select#missing").getSelectedText())
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void getTextReturnsTextsOfSelectedOptions() {
    assertThat($("#hero").getText())
      .isEqualTo("-- Select your hero --");

    $("#hero").selectOptionByValue("john mc'lain");
    assertThat($("#hero").getText())
      .isEqualTo("John Mc'Lain");
  }

  @Test
  void shouldHaveTextChecksSelectedOption() {
    $("#hero").shouldNotHave(text("John Mc'Lain").because("Option is not selected yet"));

    $("#hero").selectOptionByValue("john mc'lain");
    $("#hero").shouldHave(text("John Mc'Lain").because("Option `john mc'lain` is selected"));
  }

  @Test
  void optionValueWithApostrophe() {
    $("#hero").selectOptionByValue("john mc'lain");
    $("#hero").getSelectedOption().shouldHave(text("John Mc'Lain"));
  }

  @Test
  void optionValueWithQuote() {
    $("#hero").selectOptionByValue("arnold \"schwarzenegger\"");
    $("#hero").getSelectedOption().shouldHave(text("Arnold \"Schwarzenegger\""));
  }

  @Test
  void optionTextWithApostrophe() {
    $("#hero").selectOption("John Mc'Lain");
    $("#hero").getSelectedOption().shouldHave(value("john mc'lain"));
  }

  @Test
  void optionTextWithQuote() {
    $("#hero").selectOption("Arnold \"Schwarzenegger\"");
    $("#hero").getSelectedOption().shouldHave(value("arnold \"schwarzenegger\""));
  }

  @Test
  void optionTextWithApostropheInsideQuote() {
    $("#hero").selectOption("Mickey \"Rock'n'Roll\" Rourke");
    $("#hero").getSelectedOption().shouldHave(value("mickey rourke"));
  }

  @Test
  void selectingOptionTriggersChangeEvent() {
    $("#selectedDomain").shouldBe(empty);

    $(By.xpath("//select[@name='domain']")).selectOption("@мыло.ру");
    $("#selectedDomain").shouldHave(text("@мыло.ру"));

    $(By.xpath("//select[@name='domain']")).selectOptionByValue("myrambler.ru");
    $("#selectedDomain").shouldHave(text("@myrambler.ru"));
  }
}
