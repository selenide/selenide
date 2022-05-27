package integration;

import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exactTextCaseSensitive;
import static com.codeborne.selenide.Condition.focused;
import static com.codeborne.selenide.SetValueMethod.SEND_KEYS;
import static com.codeborne.selenide.SetValueOptions.withText;
import static org.assertj.core.api.Assertions.assertThat;

final class InputFieldTest extends ITest {
  @BeforeEach
  void setup() {
    openFile("html5_input.html?" + System.currentTimeMillis());
  }

  @Test
  void selenideClearTest() {
    SelenideElement input = $("#id1");
    assertThat(input.getValue()).isNullOrEmpty();

    input.clear();
    input.setValue(",.123");
    input.clear();
    input.setValue("456");
    assertThat(input.getValue()).isEqualTo("456");

    input.clear();
    input.setValue(",.123");
    input.clear();
    input.setValue("456");
    assertThat(input.getValue()).isEqualTo("456");

    input.setValue("789");
    input.clear();
    assertThat(input.getValue()).isEqualTo("");
  }

  @Test
  void clearMovesFocusToBody() {
    $("[name=username]").val("john");
    $("[name=password]").val("secret");
    $("[name=username]").clear();
    $("body").shouldBe(focused);
  }

  @Test
  void clearTriggersChangeEvent() {
    $("[name=username]").setValue(withText("john").usingMethod(SEND_KEYS)).pressTab();
    $("#username-mirror").shouldHave(exactTextCaseSensitive("john"));

    $("[name=password]").val("secret").pressTab();
    $("#password-mirror").shouldHave(exactTextCaseSensitive("secret"));

    $("[name=username]").clear();
    $("#username-mirror").shouldHave(exactTextCaseSensitive(""));

    $("[name=password]").clear();
    $("#password-mirror").shouldHave(exactTextCaseSensitive(""));
  }
}
