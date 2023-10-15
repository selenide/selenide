package integration;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.InvalidStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.texts;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.partialText;
import static com.codeborne.selenide.Condition.partialTextCaseSensitive;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.textCaseSensitive;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Configuration.timeout;
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
    select.selectOptionByValue("two.eu");

    select.getSelectedOption().shouldBe(selected);
    assertThat(select.getSelectedOptionValue())
      .isEqualTo("two.eu");
    assertThat(select.getSelectedOptionText())
      .isEqualTo("@two.eu");
  }

  @Test
  void userCanGetSelectedOption_none() {
    $("select#hero").getSelectedOption().shouldHave(exactText("-- Select your hero --"));
    $("select#gender").getSelectedOption().shouldHave(exactText(""));
  }

  @Test
  void userCanGetSelectedOption_selectHasNoOptions() {
    $("select#empty-select").getSelectedOption().shouldNot(exist);

    assertThatThrownBy(() -> $("select#empty-select").getSelectedOption().text())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {select#empty-select :selected}");
  }

  @Test
  void userCanGetSelectedOption_selectNotFound() {
    assertThat($("select#missing-select").getSelectedOption().exists()).isFalse();

    assertThatThrownBy(() -> $("select#missing-select").getSelectedOption().text())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {select#missing-select}");
  }

  @Test
  void userCanGetSelectedOptionValue_none() {
    assertThat($("select#hero").getSelectedOptionValue()).isEqualTo("");
    assertThat($("select#gender").getSelectedOptionValue()).isEqualTo("");
  }

  @Test
  void userCanGetSelectedOptionValue_selectHasNoOptions() {
    assertThat($("select#empty-select").getSelectedOptionValue()).isEqualTo("");
  }

  @Test
  void userCanGetSelectedOptionValue_selectNotFound() {
    assertThatThrownBy(() -> $("select#missing-select").getSelectedOptionValue())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {select#missing-select}");
  }

  @Test
  void userCanGetSelectedOptionText_none() {
    assertThat($("select#hero").getSelectedOptionText()).isEqualTo("-- Select your hero --");
    assertThat($("select#gender").getSelectedOptionText()).isEqualTo("");
  }

  @Test
  void userCanGetSelectedOptionText_selectHasNoOptions() {
    assertThat($("select#empty-select").getSelectedOptionText()).isEqualTo("");
  }

  @Test
  void userCanGetSelectedOptionText_selectNotFound() {
    assertThatThrownBy(() -> $("select#missing-select").getSelectedOptionText())
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {select#missing-select}");
  }

  @Test
  void selectOptionByValue_optionNotFound() {
    assertThatThrownBy(() -> {
      SelenideElement select = $(By.xpath("//select[@name='domain']"));
      select.selectOptionByValue("wrong-value");
    })
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {By.xpath: //select[@name='domain']/option[value:wrong-value]}");
  }

  @Test
  void selectOptionByText_optionNotFound() {
    assertThatThrownBy(() -> {
      SelenideElement select = $(By.xpath("//select[@name='domain']"));
      select.selectOption("wrong-text");
    })
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {By.xpath: //select[@name='domain']/option[text:wrong-text]}")
      .hasMessageContaining("Expected: exist");
  }

  @Test
  void selectMultipleOptionsByText_optionsNotFound() {
    assertThatThrownBy(() -> {
      SelenideElement select = $(By.xpath("//select[@name='domain']"));
      select.selectOption("wrong-text", "@one.io", "another-wrong");
    })
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith("Element not found {By.xpath: //select[@name='domain']/option[text:wrong-text,another-wrong]}")
      .hasMessageContaining("Expected: exist");
  }

  @Test
  void userCanSelectOptionByIndex() {
    SelenideElement select = $(By.xpath("//select[@name='domain']"));

    select.selectOption(0);
    assertThat(select.getSelectedOptionText())
      .isEqualTo("@one.io");

    select.selectOption(1);
    assertThat(select.getSelectedOptionText())
      .isEqualTo("@two.eu");

    select.selectOption(2);
    assertThat(select.getSelectedOptionText())
      .isEqualTo("@three.com");

    select.selectOption(3);
    assertThat(select.getSelectedOptionText())
      .isEqualTo("@four.ee");
  }

  @Test
  void throwsElementNotFoundWithOptionsIndex() {
    assertThatThrownBy(() -> $x("//select[@name='domain']").selectOption(999))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith(
        String.format("Element not found {By.xpath: //select[@name='domain']/option[index:999]}%nExpected: exist"));
  }

  @Test
  void userCanSelectOptionByText() {
    SelenideElement select = $("select[name=domain]");
    select.selectOption("@four.ee");

    select.shouldBe(visible);
    select.getSelectedOption().shouldBe(visible);
    select.getSelectedOption().shouldHave(text("@four.ee"));
    select.getSelectedOption().shouldHave(value("four.ee"));
  }

  @Test
  void userCanSelectOptionByPartialText() {
    SelenideElement select = $(By.xpath("//select[@name='domain']"));
    select.selectOptionContainingText("our.e");

    assertThat(select.getSelectedOptionText())
      .isEqualTo("@four.ee");
  }

  @Test
  void getSelectedText_cannotBeNull() {
    SelenideElement select = $("select#gender");
    assertThat(select.getSelectedOptionText()).isEqualTo("");

    select.selectOptionContainingText("emal");
    assertThat(select.getSelectedOptionText()).isEqualTo("Female");

    assertThatThrownBy(() -> $("select#missing").getSelectedOptionText())
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
    $("#hero").shouldHave(text("john mc'lain")
      .because("Option with text `John Mc'Lain` is selected"));
  }

  @Test
  void shouldHaveTextChecksSelectedOption_caseInsensitive() {
    $("#hero").selectOptionByValue("john mc'lain");

    $("#hero").shouldHave(text("JOHN MC'LAIN")
      .because("The comparison is case-insensitive by default"));
  }

  @Test
  void shouldHaveTextChecksSelectedOption_caseSensitive() {
    $("#hero").selectOptionByValue("john mc'lain");

    $("#hero").shouldHave(
      textCaseSensitive("John Mc'Lain")
        .because("Option with text `John Mc'Lain` is selected")
    );
  }

  @Test
  void shouldHaveTextChecksSelectedOptions() {
    $("#cars").selectOptionByValue("saab", "audi");

    $("#cars").shouldHave(text("saabaudi").because("Options `Audi` and `Saab` are selected"));
    $("#cars").shouldHave(textCaseSensitive("SaabAudi").because("Options `Audi` and `Saab` are selected"));
  }

  @Test
  void shouldHaveTextChecksSelectedOptions_partial() {
    $("#cars").selectOptionByValue("saab", "audi");

    $("#cars").shouldHave(partialText("audi").because("Option with text `Audi` is selected"));
    $("#cars").shouldHave(partialText("saab").because("Option with text `Saab` is selected"));
    $("#cars").shouldHave(partialTextCaseSensitive("Audi").because("Option with text `Audi` is selected"));
    $("#cars").shouldHave(partialTextCaseSensitive("Saab").because("Option with text `Saab` is selected"));
  }

  @Test
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

    $(By.xpath("//select[@name='domain']")).selectOption("@four.ee");
    $("#selectedDomain").shouldHave(text("@four.ee"));

    $(By.xpath("//select[@name='domain']")).selectOptionByValue("two.eu");
    $("#selectedDomain").shouldHave(text("@two.eu"));
  }

  @Test
  void getSelectedOptionText_isOnlyApplicableToSelect() {
    assertThatThrownBy(() -> $("h1").getSelectedOptionText())
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageStartingWith("Expected <select>, but received: <h1>");
  }

  @Test
  void getSelectedOptionValue_isOnlyApplicableToSelect() {
    assertThatThrownBy(() -> $("h1").getSelectedOptionValue())
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageStartingWith("Expected <select>, but received: <h1>");
  }

  @Test
  void selectOptionByText_disabledSelect() {
    assertThatThrownBy(() -> $("#disabled-select").selectOption("Anna"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Invalid element state [#disabled-select]: Cannot select option in a disabled select");
  }

  @Test
  void selectOptionByIndex_disabledSelect() {
    assertThatThrownBy(() -> $("#disabled-select").selectOption(0))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Invalid element state [#disabled-select]: Cannot select option in a disabled select");
  }

  @Test
  void selectOptionContainingText_notFound() {
    assertThatThrownBy(() -> $("select#gender").selectOptionContainingText("ale", "emale", "third", "fourth"))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageContaining("Element not found {select#gender/option[text containing:third,fourth]}");
  }

  @Test
  void selectOptionContainingText_disabledSelect() {
    assertThatThrownBy(() -> $("#disabled-select").selectOptionContainingText("Arnold"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Invalid element state [#disabled-select]: Cannot select option in a disabled select");
  }

  @Test
  void selectOptionByValue_disabledSelect() {
    assertThatThrownBy(() -> $("#disabled-select").selectOptionByValue("mickey rourke"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Invalid element state [#disabled-select]: Cannot select option in a disabled select");
  }

  @Test
  void selectOptionByText_disabledOption() {
    assertThatThrownBy(() -> $("#cars").selectOption("Zhiguli"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Invalid element state [#cars/option[text:Zhiguli]]: Cannot select a disabled option");
  }

  @Test
  void selectMultipleOptionsByText_disabledOption() {
    assertThatThrownBy(() -> $("#cars").selectOption("Volvo", "Audi", "Zhiguli", "Saab"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Invalid element state [#cars/option[text:Zhiguli]]: Cannot select a disabled option");
  }

  @Test
  void selectMultipleOptionsByText_disabledOptions() {
    assertThatThrownBy(() -> $("#cars").selectOption("Lada", "Volvo", "Audi", "Zhiguli", "Saab"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Invalid element state [#cars/option[text:Lada,Zhiguli]]: Cannot select a disabled option");
  }

  @Test
  void selectOptionByText_nonSelect() {
    assertThatThrownBy(() -> $("#greetings").selectOption("Lada"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Cannot select option from a non-select element");
  }

  @Test
  void selectOptionByIndex_disabledOption() {
    assertThatThrownBy(() -> $("#cars").selectOption(4))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Invalid element state [#cars/option[index:4]]: Cannot select a disabled option");
  }

  @Test
  void selectMultipleOptionsByIndex_disabledOption() {
    assertThatThrownBy(() -> $("#cars").selectOption(1, 2, 4))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Invalid element state [#cars/option[index:4]]: Cannot select a disabled option");
  }

  @Test
  void selectMultipleOptionsByIndex_disabledOptions() {
    assertThatThrownBy(() -> $("#cars").selectOption(1, 2, 4, 5))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Invalid element state [#cars/option[index:4,5]]: Cannot select a disabled option");
  }

  @Test
  void selectOptionByIndex_nonSelect() {
    assertThatThrownBy(() -> $("#greetings").selectOption(1))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessageContaining("Cannot select option from a non-select element");
  }

  @Test
  void selectOptionContainingText_disabledOption() {
    assertThatThrownBy(() -> $("#cars").selectOptionContainingText("higul"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Invalid element state [#cars/option[text containing:higul]]: Cannot select a disabled option");
  }

  @Test
  void selectMultipleOptionsContainingText_disabledOptions() {
    assertThatThrownBy(() -> $("#cars").selectOptionContainingText("higul", "ada"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Invalid element state [#cars/option[text containing:higul,ada]]: Cannot select a disabled option");
  }

  @Test
  public void selectOptionContainingText_nonSelect() {
    assertThatThrownBy(() -> $("#greetings").selectOptionContainingText("Kelly Sildaru"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Cannot select option from a non-select element");
  }

  @Test
  void selectOptionByValue_disabledOption() {
    assertThatThrownBy(() -> $("#cars").selectOptionByValue("zhiguli"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Invalid element state [#cars/option[value:zhiguli]]: Cannot select a disabled option");
  }

  @Test
  void selectMultipleOptionsByValue_disabledOption() {
    assertThatThrownBy(() -> $("#cars").selectOptionByValue("opel", "zhiguli", "audi"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Invalid element state [#cars/option[value:zhiguli]]: Cannot select a disabled option");
  }

  @Test
  void selectMultipleOptionsByValue_disabledOptions() {
    assertThatThrownBy(() -> $("#cars").selectOptionByValue("opel", "zhiguli", "audi", "lada"))
      .isInstanceOf(InvalidStateException.class)
      .hasMessageContaining("Invalid element state [#cars/option[value:zhiguli,lada]]: Cannot select a disabled option");
  }

  @Test
  public void selectOptionByValue_nonSelect() {
    assertThatThrownBy(() -> $("#greetings").selectOptionByValue("opel"))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Cannot select option from a non-select element");
  }

  @Test
  void canCheckSelectOptions() {
    $("#cars").getOptions().shouldHave(texts("Volvo", "Saab", "Opel", "Audi", "Zhiguli", "Lada"));

    $("#cars").selectOption("Volvo");
    $("#cars").getSelectedOptions().shouldHave(texts("Volvo"));
  }

  @Test
  void canCheckOptionsOfDisabledSelect() {
    $("#disabled-select").getOptions().last(2).shouldHave(texts("Elsa", "Anna"));

    $("#disabled-select").getSelectedOptions().shouldHave(size(1));
    $("#disabled-select").getSelectedOption().shouldHave(exactValue(""));
    $("#disabled-select").getSelectedOption().shouldHave(text("-- Select the frozen --"));
  }

  @Test
  void canCheckTextOfDisabledSelect() {
    $("#disabled-select").shouldHave(text("-- Select the frozen --"));
  }

  @Test
  void canWaitUntilSelectsGetsEnabled() {
    timeout = 1000;
    $("#disabled-select").shouldBe(disabled);
    $("#unfreeze-me").click();
    $("#disabled-select").selectOption("Anna");
    $("#disabled-select").getSelectedOption().shouldHave(text("Anna"));
    $("#disabled-select").shouldBe(enabled);
  }
}
