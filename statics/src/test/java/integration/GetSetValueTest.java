package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.ElementShould;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.partialValue;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class GetSetValueTest extends IntegrationTest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void getValue_canBeEmpty() {
    assertThat($(By.name("password")).val()).isEqualTo("");
    assertThat($(By.name("password")).getValue()).isEqualTo("");
  }

  @Test
  void getValue_canBeNull() {
    assertThat($("h2").val()).isNull();
    assertThat($("h2").getValue()).isNull();
  }

  @Test
  void getValue_throwsError_ifElementNotFound() {
    assertThatThrownBy(() -> $("#missing").val())
      .isInstanceOf(ElementNotFound.class);
    assertThatThrownBy(() -> $("#missing").getValue())
      .isInstanceOf(ElementNotFound.class);
  }

  @Test
  void userCanSetValueToTextField() {
    $(By.name("password")).setValue("john");
    $(By.name("password")).val("sherlyn");
    $(By.name("password")).shouldHave(value("sherlyn"));
    $(By.name("password")).shouldHave(value("sherlyn"), ofMillis(1000));
    assertThat($(By.name("password")).val()).isEqualTo("sherlyn");
  }

  @Test
  void userCanSetValueToTextArea() {
    Configuration.fastSetValue = false;
    $("#empty-text-area").val("text for textarea");
    $("#empty-text-area").shouldHave(value("text for textarea"));

    Configuration.fastSetValue = true;
    $("#empty-text-area").val("another text");
    $("#empty-text-area").shouldHave(value("another text"));
  }

  @Test
  void userCannotSetValueLongerThanMaxLength() {
    $(By.name("password")).shouldHave(attribute("maxlength", "24"));
    $(By.name("password")).val("123456789_123456789_123456789_");
    $(By.name("password")).shouldHave(value("123456789_123456789_1234"));
  }

  @Test
  void valueCheckIgnoresDifferenceInInvisibleCharacters() {
    $(By.name("password")).setValue("john   \u00a0 Malkovich");
    $(By.name("password")).shouldHave(value("john Malkovich"));

    $("#empty-text-area").setValue("john   \u00a0 \r\nMalkovich\n");
    $("#empty-text-area").shouldHave(value("john Malkovich"));
  }

  @Test
  void canCheckValueSubstring() {
    $(By.name("password")).setValue("John Malkovich");
    $(By.name("password")).shouldHave(partialValue("ohn Malkov"));

    $("#empty-text-area").setValue("John Malkovich");
    $("#empty-text-area").shouldHave(partialValue("hn Malk"));
  }

  @Test
  void value_errorMessage() {
    $("#empty-text-area").setValue("Bilbo Baggins");

    assertThatThrownBy(() -> $("#empty-text-area").shouldHave(value("Bilbo Sumkins")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have value=\"Bilbo Sumkins\" {#empty-text-area}")
      .hasMessageContaining("Actual value: value=\"Bilbo Baggins\"");
  }

  @Test
  void partialValue_errorMessage() {
    $("#empty-text-area").setValue("Bilbo Baggins");

    assertThatThrownBy(() -> $("#empty-text-area").shouldHave(partialValue("Bilbo Big")))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should have partial value \"Bilbo Big\" {#empty-text-area}")
      .hasMessageContaining("Actual value: value=\"Bilbo Baggins\"");
  }

  @Test
  void userCanAppendValueToTextField() {
    $(By.name("password")).val("Sherlyn");
    $(By.name("password")).append(" theron");
    $(By.name("password")).shouldHave(value("Sherlyn theron"));
    assertThat($(By.name("password")).val()).isEqualTo("Sherlyn theron");
  }
}
