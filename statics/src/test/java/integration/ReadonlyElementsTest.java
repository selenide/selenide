package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.ex.InvalidStateException;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.readonly;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

final class ReadonlyElementsTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_readonly_elements.html");
    Configuration.timeout = 1000;
  }

  @BeforeEach
  @AfterEach
  void cleanUp() {
    Configuration.fastSetValue = false;
  }

  @Test
  void cannotSetValueToReadonlyField_slowSetValue() {
    final List<String> exceptionMessages = Arrays.asList(
      "Element is read-only and so may not be used for actions",
      "Element must be user-editable in order to clear it",
      "You may only edit editable elements",
      "Invalid element state: invalid element state",
      "Element is read-only: <input name=\"username\">"
    );

    Configuration.fastSetValue = false;

    assertThat(verifySetValueThrowsException())
      .is(anyOf(getExceptionMessagesCondition(exceptionMessages)));
  }

  private String verifySetValueThrowsException() {
    try {
      $(By.name("username")).val("another-username");
      fail("should throw InvalidStateException where setting value to readonly/disabled element");
      return null;
    } catch (InvalidStateException expected) {
      $(By.name("username")).shouldBe(empty);
      $(By.name("username")).shouldHave(exactValue(""));
      return expected.getMessage();
    }
  }

  private Condition<String> getExceptionMessagesCondition(final List<String> exceptionMessages) {
    return new Condition<>(exception ->
      exceptionMessages.stream().anyMatch(exception::contains),
      "exceptionMessages");
  }

  @Test
  void cannotSetValueToDisabledField_slowSetValue() {
    final List<String> exceptionMessages = Arrays.asList(
      "Element must be user-editable in order to clear it",
      "You may only edit editable elements",
      "You may only interact with enabled elements",
      "Element is not currently interactable and may not be manipulated",
      "Invalid element state: invalid element state: Element is not currently interactable and may not be manipulated",
      "Element is disabled");

    Configuration.fastSetValue = false;

    assertThat(verifySetValue2ThrowsException())
      .is(anyOf(getExceptionMessagesCondition(exceptionMessages)));
  }

  private String verifySetValue2ThrowsException() {
    try {
      $(By.name("password")).setValue("another-pwd");
      fail("should throw InvalidStateException where setting value to readonly/disabled element");
      return null;
    } catch (InvalidStateException expected) {
      $(By.name("password")).shouldBe(empty);
      $(By.name("password")).shouldHave(exactValue(""));
      return expected.getMessage();
    }
  }

  @Test
  void cannotSetValueToReadonlyField_fastSetValue() {
    final List<String> exceptionMessages = Arrays.asList(
      "Cannot change value of readonly element",
      "Element must be user-editable in order to clear it");

    Configuration.fastSetValue = true;

    assertThat(verifySetValueThrowsException())
      .is(anyOf(getExceptionMessagesCondition(exceptionMessages)));
  }

  @Test
  void cannotSetValueToDisabledField_fastSetValue() {
    final List<String> exceptionMessages = Arrays.asList(
      "Cannot change value of disabled element",
      "Element is not currently interactable and may not be manipulated");

    Configuration.fastSetValue = true;

    assertThat(verifySetValue2ThrowsException())
      .is(anyOf(getExceptionMessagesCondition(exceptionMessages)));
  }

  @Test
  void cannotSetValueToReadonlyTextArea() {
    assertThatThrownBy(() -> $("#text-area").val("textArea value"))
      .isInstanceOf(InvalidStateException.class);
  }

  @Test
  void cannotSetValueToDisabledTextArea() {
    assertThatThrownBy(() -> $("#text-area-disabled").val("textArea value"))
      .isInstanceOf(InvalidStateException.class);
  }

  @Test
  void cannotChangeValueOfDisabledCheckbox() {
    assertThatThrownBy(() -> $(By.name("disabledCheckbox")).setSelected(false))
      .isInstanceOf(InvalidStateException.class);
  }

  @Test
  void cannotSetValueToReadonlyCheckbox() {
    assertThatThrownBy(() -> $(By.name("rememberMe")).setSelected(true))
      .isInstanceOf(InvalidStateException.class);
  }

  @Test
  void cannotSetValueToReadonlyRadiobutton() {
    assertThatThrownBy(() -> $(By.name("me")).selectRadio("margarita"))
      .isInstanceOf(InvalidStateException.class);
  }

  @Test
  void waitsUntilInputGetsEditable_slowSetValue() {
    $("#enable-inputs").click();

    Configuration.fastSetValue = false;
    $(By.name("username")).val("another-username");
    $(By.name("username")).shouldHave(exactValue("another-username"));
  }

  @Test
  void waitsUntilInputGetsEditable_fastSetValue() {
    $("#enable-inputs").click();

    Configuration.fastSetValue = true;
    $(By.name("username")).val("another-username");
    $(By.name("username")).shouldHave(exactValue("another-username"));
  }

  @Test
  void waitsUntilTextAreaGetsEditable() {
    $("#enable-inputs").click();
    $("#text-area").val("TextArea value");
    $("#text-area").shouldHave(exactValue("TextArea value"));
  }

  @Test
  void waitsUntilCheckboxGetsEditable() {
    $("#enable-inputs").click();
    $(By.name("rememberMe")).setSelected(true);
    $(By.name("rememberMe")).shouldBe(selected);
  }

  @Test
  void waitsUntilRadiobuttonGetsEditable() {
    $("#enable-inputs").click();
    $(By.name("me")).selectRadio("margarita");
    $(Selectors.byValue("margarita")).shouldBe(selected);
  }

  @Test
  void readonlyAttributeIsShownInErrorMessage() {
    assertThatThrownBy(() -> $(By.name("username")).shouldNotHave(readonly))
      .hasMessageMatching("(?s).*<input.*readonly.*");
  }
}
