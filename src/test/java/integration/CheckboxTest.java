package integration;

import com.codeborne.selenide.ex.ElementShould;
import com.codeborne.selenide.ex.InvalidStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.checked;
import static com.codeborne.selenide.Condition.selected;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

final class CheckboxTest extends ITest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_selects_without_jquery.html");
  }

  @Test
  void userCanSelectCheckbox() {
    $(By.name("rememberMe")).shouldNotBe(selected);
    $(By.name("rememberMe")).shouldNotBe(checked);

    $(By.name("rememberMe")).click();

    $(By.name("rememberMe")).shouldBe(selected);
    $(By.name("rememberMe")).shouldBe(checked);
    assertThat($(By.name("rememberMe"))).hasToString("{By.name: rememberMe}");
  }

  @Test
  void userCanCheckCheckbox() {
    $(By.name("rememberMe")).setSelected(true);
    $(By.name("rememberMe")).shouldBe(selected);
    $(By.name("rememberMe")).shouldBe(checked);

    $(By.name("rememberMe")).setSelected(true);
    $(By.name("rememberMe")).shouldBe(selected);
    $(By.name("rememberMe")).shouldBe(checked);
  }

  @Test
  void userCanUnCheckCheckbox() {
    $(By.name("rememberMe")).setSelected(true);
    $(By.name("rememberMe")).shouldBe(selected);

    $(By.name("rememberMe")).setSelected(false);
    $(By.name("rememberMe")).shouldNotBe(selected);

    $(By.name("rememberMe")).setSelected(false);
    $(By.name("rememberMe")).shouldNotBe(selected);
  }

  @Test
  void actual_value_selected() {
    assertThatThrownBy(() -> $(By.name("rememberMe")).shouldBe(selected))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be selected {By.name: rememberMe}")
      .hasMessageContaining("Element: '<input name=\"rememberMe\" type=\"checkbox\" value=\"on\"></input>'")
      .hasMessageContaining("Actual value: not selected");
  }

  @Test
  void actual_value_checked() {
    assertThatThrownBy(() -> $(By.name("rememberMe")).shouldBe(checked))
      .isInstanceOf(ElementShould.class)
      .hasMessageStartingWith("Element should be checked {By.name: rememberMe}")
      .hasMessageContaining("Element: '<input name=\"rememberMe\" type=\"checkbox\" value=\"on\"></input>'")
      .hasMessageContaining("Actual value: unchecked");
  }

  @Test
  void userCannotSetSelectOnTextInput() {
    assertThatThrownBy(() -> $("#username").setSelected(false))
      .isInstanceOf(InvalidStateException.class);
  }

  @Test
  void userCannotSetSelectOnArbitraryElement() {
    assertThatThrownBy(() -> $("#username-mirror").setSelected(false))
      .isInstanceOf(InvalidStateException.class);
  }

  @Test
  void userCannotCheckInvisibleCheckbox() {
    assertThatThrownBy(() -> $(By.name("invisibleCheckbox")).setSelected(false))
      .isInstanceOf(InvalidStateException.class);
  }
}
