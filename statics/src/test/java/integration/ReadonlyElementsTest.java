package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.ElementShouldNot;
import com.codeborne.selenide.ex.InvalidStateException;
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

    assertThatThrownBy(() -> {
      $(By.name("username")).val("another-username");
    })
      .as("should throw InvalidStateException where setting value to readonly/disabled element")
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be editable {By.name: username}");
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

    assertThatThrownBy(() -> {
      $(By.name("password")).setValue("another-pwd");
    })
      .as("should throw InvalidStateException where setting value to readonly/disabled element")
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be editable {By.name: password}");
    $(By.name("password")).shouldBe(empty);
    $(By.name("password")).shouldHave(exactValue(""));
  }

  @Test
  void cannotSetValueToReadonlyField_fastSetValue() {
    Configuration.fastSetValue = true;

    assertThatThrownBy(() -> {
      $(By.name("username")).val("another-username");
    })
      .as("should throw InvalidStateException where setting value to readonly/disabled element")
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be editable {By.name: username}");
    $(By.name("username")).shouldBe(empty);
    $(By.name("username")).shouldHave(exactValue(""));
  }

  @Test
  void cannotSetValueToDisabledField_fastSetValue() {
    Configuration.fastSetValue = true;

    assertThatThrownBy(() -> {
      $(By.name("password")).setValue("another-pwd");
    })
      .as("should throw InvalidStateException where setting value to readonly/disabled element")
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be editable {By.name: password}");
    $(By.name("password")).shouldBe(empty);
    $(By.name("password")).shouldHave(exactValue(""));
  }

  @Test
  void cannotSetValueToReadonlyTextArea() {
    assertThatThrownBy(() -> $("#text-area").val("textArea value"))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be editable {#text-area}");
  }

  @Test
  void cannotSetValueToDisabledTextArea() {
    assertThatThrownBy(() -> $("#text-area-disabled").val("textArea value"))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be editable {#text-area-disabled}");
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
    assertThatThrownBy(() -> $(By.name("username")).shouldNotBe(readonly))
      .isInstanceOf(ElementShouldNot.class)
      .hasMessageStartingWith("Element should not be readonly {By.name: username}")
      .hasMessageMatching("(?s).*<input.*readonly.*");
  }
}
