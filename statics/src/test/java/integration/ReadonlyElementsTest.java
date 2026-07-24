package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import com.codeborne.selenide.ex.InvalidStateError;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import java.util.List;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Condition.readonly;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Selenide.$;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    Configuration.fastSetValue = false;
    Configuration.timeout = 10;

    assertThatThrownBy(() -> {
      $(By.name("username")).val("another-username");
    })
      .as("should throw InvalidStateException where setting value to readonly/disabled element")
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be editable {By.name: username}")
      .hasMessageContaining("Timeout: 10ms");
    $(By.name("username")).shouldBe(empty);
    $(By.name("username")).shouldHave(exactValue(""));
  }

  private Condition<String> getExceptionMessagesCondition(final List<String> exceptionMessages) {
    return new Condition<>(exception ->
      exceptionMessages.stream().anyMatch(exception::contains),
      "exceptionMessages");
  }

  @Test
  void cannotSetValueToDisabledField_slowSetValue() {
    Configuration.fastSetValue = false;
    Configuration.timeout = 10;

    assertThatThrownBy(() -> {
      $(By.name("password")).setValue("another-pwd");
    })
      .as("should throw InvalidStateException where setting value to readonly/disabled element")
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be editable {By.name: password}")
      .hasMessageContaining("Actual value: disabled")
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Page source:")
      .hasMessageContaining("Timeout: 10ms");
    $(By.name("password")).shouldBe(empty);
    $(By.name("password")).shouldHave(exactValue(""));
  }

  @Test
  void cannotSetValueToReadonlyField_fastSetValue() {
    Configuration.fastSetValue = true;
    Configuration.timeout = 5;

    assertThatThrownBy(() -> {
      $(By.name("username")).val("another-username");
    })
      .as("should throw InvalidStateException where setting value to readonly/disabled element")
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be editable {By.name: username}")
      .hasMessageContaining("Actual value: readonly=\"true\"")
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Page source:")
      .hasMessageContaining("Timeout: 5ms");
    $(By.name("username")).shouldBe(empty);
    $(By.name("username")).shouldHave(exactValue(""));
  }

  @Test
  void cannotSetValueToDisabledField_fastSetValue() {
    Configuration.fastSetValue = true;
    Configuration.timeout = 10;

    assertThatThrownBy(() -> {
      $(By.name("password")).setValue("another-pwd");
    })
      .as("should throw InvalidStateException where setting value to readonly/disabled element")
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be editable {By.name: password}")
      .hasMessageContaining("Timeout: 10ms");
    $(By.name("password")).shouldBe(empty);
    $(By.name("password")).shouldHave(exactValue(""));
  }

  @Test
  void cannotSetValueToReadonlyTextArea() {
    Configuration.timeout = 5;

    assertThatThrownBy(() -> $("#text-area").val("textArea value"))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be editable {#text-area}")
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Page source:")
      .hasMessageContaining("Timeout: 5ms");
  }

  @Test
  void cannotSetValueToDisabledTextArea() {
    Configuration.timeout = 5;

    assertThatThrownBy(() -> $("#text-area-disabled").val("textArea value"))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be editable {#text-area-disabled}")
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Page source:")
      .hasMessageContaining("Timeout: 5ms");
  }

  @Test
  void cannotChangeValueOfDisabledCheckbox() {
    Configuration.timeout = 5;

    assertThatThrownBy(() -> $(By.name("disabledCheckbox")).setSelected(false))
      .isInstanceOf(InvalidStateError.class)
      .hasMessageStartingWith("Invalid element state [By.name: disabledCheckbox]: Cannot change value of readonly/disabled element")
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Page source:")
      .hasMessageContaining("Timeout: 5ms");
  }

  @Test
  void cannotSetValueToReadonlyCheckbox() {
    Configuration.timeout = 5;
    assertThatThrownBy(() -> $(By.name("rememberMe")).setSelected(true))
      .isInstanceOf(InvalidStateError.class)
      .hasMessageStartingWith("Invalid element state [By.name: rememberMe]: Cannot change value of readonly/disabled element")
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Page source:")
      .hasMessageContaining("Timeout: 5ms");
  }

  @Test
  void cannotSetValueToReadonlyRadioButton() {
    Configuration.timeout = 10;
    assertThatThrownBy(() -> $(By.name("me")).selectRadio("margarita"))
      .isInstanceOf(InvalidStateError.class)
      .hasMessageStartingWith("Invalid element state [By.name: me]: Cannot select readonly radio button")
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Page source:")
      .hasMessageContaining("Timeout: 10ms");
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
    Configuration.timeout = 5;

    assertThatThrownBy(() -> $(By.name("username")).shouldNotBe(readonly))
      .isInstanceOf(ElementShouldNot.class)
      .hasMessageStartingWith("Element should not be readonly {By.name: username}")
      .hasMessageMatching("(?s).*Element:.*<input.*readonly.*")
      .hasMessageContaining("Actual value: readonly=\"true\"")
      .hasMessageContaining("Screenshot:")
      .hasMessageContaining("Page source:")
      .hasMessageContaining("Timeout: 5ms");
  }
}
