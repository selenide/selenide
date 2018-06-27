package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.ex.InvalidStateException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.$;
import static org.hamcrest.CoreMatchers.containsString;

class ReadonlyElementsTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_readonly_elements.html");
    timeout = 10 * averageSeleniumCommandDuration;
  }

  @AfterEach
  void cleanUp() {
    Configuration.fastSetValue = false;
  }

  @Test
  void cannotSetValueToReadonlyField_slowSetValue() {
    Configuration.fastSetValue = false;

    MatcherAssert.assertThat(verifySetValueThrowsException(), CoreMatchers.anyOf(
      containsString("Element is read-only and so may not be used for actions"),
      containsString("Element must be user-editable in order to clear it"),
      containsString("You may only edit editable elements")
    ));
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

  @Test
  void cannotSetValueToDisabledField_slowSetValue() {
    Configuration.fastSetValue = false;

    MatcherAssert.assertThat(verifySetValue2ThrowsException(), CoreMatchers.anyOf(
      containsString("Element must be user-editable in order to clear it"),
      containsString("You may only edit editable elements"),
      containsString("You may only interact with enabled elements"),
      containsString("Element is not currently interactable and may not be manipulated")
    ));
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
    Configuration.fastSetValue = true;
    MatcherAssert.assertThat(verifySetValueThrowsException(), CoreMatchers.anyOf(
      containsString("Cannot change value of readonly element"),
      containsString("Element must be user-editable in order to clear it")
    ));
  }

  @Test
  void cannotSetValueToDisabledField_fastSetValue() {
    Configuration.fastSetValue = true;
    MatcherAssert.assertThat(verifySetValue2ThrowsException(), CoreMatchers.anyOf(
      containsString("Cannot change value of disabled element"),
      containsString("Element is not currently interactable and may not be manipulated")
    ));
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
}
