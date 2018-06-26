package integration;

import com.codeborne.selenide.ex.InvalidStateException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.checked;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Selenide.$;

class CheckboxTest extends IntegrationTest {
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
    Assertions.assertEquals("<input name=\"rememberMe\" type=\"checkbox\" value=\"on\" selected:true></input>",
      $(By.name("rememberMe")).toString());
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
  void userCannotSetSelectOnTextInput() {
    Assertions.assertThrows(InvalidStateException.class,
      () -> $("#username").setSelected(false));
  }

  @Test
  void userCannotSetSelectOnArbitryElement() {
    Assertions.assertThrows(InvalidStateException.class,
      () -> $("#username-mirror").setSelected(false));
  }

  @Test
  void userCannotCheckInvisibleCheckbox() {
    Assertions.assertThrows(InvalidStateException.class,
      () -> $(By.name("invisibleCheckbox")).setSelected(false));
  }
}
