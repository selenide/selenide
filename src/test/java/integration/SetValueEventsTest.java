package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.visible;

final class SetValueEventsTest extends ITest {
  @BeforeEach
  void openAndInitTestPage() {
    openFile("page_with_inputevents_on_clear.html");

    $("#username_input").setValue("abc");
    $("#usernameHint").shouldBe(visible);
  }

  @Test
  void setValueShouldTrigger() {
    $("#username_input").setValue("");
    $("#usernameHint").should(disappear);
  }

  @Test
  void setValueNullShouldTrigger() {
    $("#username_input").setValue((String) null);
    $("#usernameHint").should(disappear);
  }

  @Test
  void clearTriggersBlurEvent() {
    $("#username_input").clear();
    $("#usernameHint").should(disappear);
  }
}
