package integration;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ex.ElementNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.checked;
import static com.codeborne.selenide.Condition.exactValue;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.getSelectedRadio;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class RadioTest extends IntegrationTest {
  @BeforeEach
  void openTestPage() {
    openFile("page_with_selects_without_jquery.html");
    assertThat(getSelectedRadio(By.name("me")))
      .isNull();
  }

  @Test
  void userCanSelectRadioButtonByValue() {
    $(By.name("me")).selectRadio("cat");
    getSelectedRadio(By.name("me")).shouldHave(exactValue("cat"));
  }

  @Test
  void userCanSelectRadioButtonByValueOldWay() {
    $(By.name("me")).selectRadio("cat");
    assertThat(getSelectedRadio(By.name("me")).getAttribute("value"))
      .isEqualTo("cat");
  }

  @Test
  void userCanSelectRadioButtonUsingSetValue() {
    Configuration.versatileSetValue = true;
    $(byName("me")).setValue("margarita");
    getSelectedRadio(By.name("me")).shouldHave(exactValue("margarita"));
  }

  @Test
  void selenideElement_selectRadio() {
    $(By.name("me")).selectRadio("margarita");
    getSelectedRadio(By.name("me")).shouldHave(exactValue("margarita"));
  }

  @Test
  void assertCheckedRadioButton() {
    $("[name=me][value=margarita]").shouldNotBe(checked);

    $(byName("me")).selectRadio("margarita");

    $("[name=me][value=margarita]").shouldBe(checked);
    $("[name=me][value=margarita]").shouldHave(attribute("checked")); // just an alternative
  }

  @Test
  void selenideElement_selectRadio_elementNotFound() {
    assertThatThrownBy(() -> $(By.id("unknownId")).selectRadio("margarita"))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith(String.format("Element not found {By.id: unknownId}%nExpected: value 'margarita'"));
  }

  @Test
  void selenideElement_selectRadio_valueNotFound() {
    assertThatThrownBy(() -> $(By.name("me")).selectRadio("unknown-value"))
      .isInstanceOf(ElementNotFound.class)
      .hasMessageStartingWith(String.format("Element not found {By.name: me}%nExpected: value 'unknown-value'"));
  }
}
