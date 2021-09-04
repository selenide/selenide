package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.InvalidStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.textCaseSensitive;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.byValue;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.executeJavaScript;
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
    $("#hero").shouldHave(text("john mc'lain").because("Option with text `John Mc'Lain` is selected"));
    $("#hero").shouldHave(textCaseSensitive("John Mc'Lain").because("Option with text `John Mc'Lain` is selected"));
  }

  @Test
  void shouldHaveTextChecksSelectedOptions() {
    $("#cars").selectOptionByValue("saab", "audi");

    $("#cars").shouldHave(text("audi").because("Option with text `Audi` is selected"));
    $("#cars").shouldHave(text("saab").because("Option with text `Saab` is selected"));
    $("#cars").shouldHave(textCaseSensitive("Audi").because("Option with text `Audi` is selected"));
    $("#cars").shouldHave(textCaseSensitive("Saab").because("Option with text `Saab` is selected"));
  }

  @Test()
  void throwsAssertionErrorForSelectedElementsWithDifferentTextOrCase() {
    $("#hero").selectOptionByValue("john mc'lain");
    assertThatThrownBy(() -> $("#hero").shouldHave(text("Denzel Washington")))
      .isInstanceOf(AssertionError.class);
    assertThatThrownBy(() -> $("#hero").shouldHave(textCaseSensitive("john mc'lain")))
      .isInstanceOf(AssertionError.class);

    $("#cars").selectOptionByValue("saab", "audi");
    assertThatThrownBy(() -> $("#cars").shouldHave(text("volvo")))
      .isInstanceOf(AssertionError.class);
    assertThatThrownBy(() -> $("#cars").shouldHave(textCaseSensitive("audi")))
      .isInstanceOf(AssertionError.class);
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

  @Test
  void throwsExceptionWhenSelectingFromDisabledSelect() {
    SelenideElement select = $("#hero");
    executeJavaScript("arguments[0].setAttribute('disabled','')", select);
    select.shouldBe(disabled);

    assertThatThrownBy(() -> select.selectOption("Denzel Washington"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Cannot select anything in a disabled select element: {#hero}");
    assertThatThrownBy(() -> select.selectOption(0))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Cannot select anything in a disabled select element: {#hero}");
    assertThatThrownBy(() -> select.selectOptionContainingText("Arnold"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Cannot select anything in a disabled select element: {#hero}");
    assertThatThrownBy(() -> select.selectOptionByValue("mickey rourke"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Cannot select anything in a disabled select element: {#hero}");
  }

  @Test
  void throwsExceptionWhenSelectingDisabledOptionByText() {
    //  single select
    SelenideElement select = $("#hero");
    SelenideElement disabledOptionOne = select.$("#denzel-washington");
    executeJavaScript("arguments[0].setAttribute('disabled','')", disabledOptionOne);
    disabledOptionOne.shouldBe(disabled);

    assertThatThrownBy(() -> select.selectOption("Denzel Washington"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Cannot select a disabled option: #hero/option[text:Denzel Washington]");

    //  multiple select
    SelenideElement selectMultiple = $("#cars");
    SelenideElement disabledOptionTwo = selectMultiple.$(byText("Saab"));
    executeJavaScript("arguments[0].setAttribute('disabled','')", disabledOptionTwo);
    disabledOptionTwo.shouldBe(disabled);

    assertThatThrownBy(() -> selectMultiple.selectOption("Volvo", "Audi", "Saab"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Cannot select a disabled option: #cars/option[text:Saab]");
  }

  @Test
  void throwsExceptionWhenSelectingDisabledOptionByIndex() {
    //  single select
    SelenideElement select = $("#hero");
    SelenideElement disabledOptionOne = select.$x(".//option[2]");
    executeJavaScript("arguments[0].setAttribute('disabled','')", disabledOptionOne);
    disabledOptionOne.shouldBe(disabled);

    assertThatThrownBy(() -> select.selectOption(1))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Cannot select a disabled option: #hero/option[index:1]");

    //  multiple select
    SelenideElement selectMultiple = $("#cars");
    SelenideElement disabledOptionTwo = selectMultiple.$x(".//option[2]");
    executeJavaScript("arguments[0].setAttribute('disabled','')", disabledOptionTwo);
    disabledOptionTwo.shouldBe(disabled);

    assertThatThrownBy(() -> selectMultiple.selectOption(0, 1, 2))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Cannot select a disabled option: #cars/option[index:1]");
  }

  @Test
  void throwsExceptionWhenSelectingDisabledOptionContainingText() {
    SelenideElement select = $("#hero");
    SelenideElement disabledOption = select.$("#denzel-washington");
    executeJavaScript("arguments[0].setAttribute('disabled','')", disabledOption);
    disabledOption.shouldBe(disabled);

    assertThatThrownBy(() -> select.selectOptionContainingText("Denzel"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Cannot select a disabled option containing text: Denzel");
  }

  @Test
  void throwsExceptionWhenSelectingDisabledOptionByValue() {
    //  single select
    SelenideElement select = $("#hero");
    SelenideElement disabledOptionOne = select.$(byValue("mickey rourke"));
    executeJavaScript("arguments[0].setAttribute('disabled','')", disabledOptionOne);
    disabledOptionOne.shouldBe(disabled);

    assertThatThrownBy(() -> select.selectOptionByValue("mickey rourke"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Cannot select a disabled option: #hero/option[value:mickey rourke]");

    //  multiple select
    SelenideElement selectMultiple = $("#cars");
    SelenideElement disabledOptionTwo = selectMultiple.$(byText("Saab"));
    executeJavaScript("arguments[0].setAttribute('disabled','')", disabledOptionTwo);
    disabledOptionTwo.shouldBe(disabled);

    assertThatThrownBy(() -> selectMultiple.selectOptionByValue("volvo", "audi", "saab"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Cannot select a disabled option: #cars/option[value:saab]");

    assertThatThrownBy(() -> select.selectOptionByValue("somevalue"))
      .isInstanceOf(ElementNotFound.class);
    assertThatThrownBy(() -> select.selectOption(10))
      .isInstanceOf(ElementNotFound.class);
    assertThatThrownBy(() -> select.selectOption("TEXT"))
      .isInstanceOf(ElementNotFound.class);
    assertThatThrownBy(() -> select.selectOptionContainingText("TEXT"))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Cannot locate option containing text: TEXT");
  }
}
