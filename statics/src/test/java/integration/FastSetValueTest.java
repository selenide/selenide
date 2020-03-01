package integration;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

class FastSetValueTest extends IntegrationTest {
  @BeforeEach
  void openTestPageWithJQuery() {
    openFile("page_with_inputs_and_hints.html");
  }

  @Test
  void standardSetValueTriggersBlurCorrectly() {
    Configuration.fastSetValue = false;
    $("#username").setValue("john");
    $("#usernameHint").should(appear);

    $("#password").setValue("admin");
    $("#usernameHint").should(disappear);
    $("#passwordHint").should(appear);
  }

  @Test
  void fastSetValueTriggersBlurCorrectly() {
    Configuration.fastSetValue = true;
    $("#username").setValue("john");
    $("#usernameHint").should(appear);

    $("#password").setValue("admin");
    $("#usernameHint").should(disappear);
    $("#passwordHint").should(appear);
  }

  @Test
  void fastSetValue_withoutChangeEvent() {
    Configuration.fastSetValue = true;
    $("#username").setValue("john");
    $("#usernameHint").should(appear);

    $("#password").setValue("admin");
    $("#usernameHint").should(disappear);
    $("#passwordHint").should(appear);
  }

  @Test
  void fastSetValue_forMaterialDesign() {
    Configuration.fastSetValue = true;
    open("https://rusakovstas.github.io/material-autocomplete/");
    $(byText("Вбейте что нибудь или скопируйте")).closest("div").$("input").setValue("123");
    $(byText("Поймали логику!")).shouldBe(visible);
  }
}
