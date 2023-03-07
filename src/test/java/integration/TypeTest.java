package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.attribute;

class TypeTest extends ITest {
  @BeforeEach
  void openAndInitTestPage() {
    openFile("page_with_inputevents_on_clear.html");
  }

  @Test
  void setValueShouldTrigger() {
    $("#username_input").setValue("test");
    $("#username_input").type("abc");
    $("#username_input").shouldHave(attribute("value", "abc"));
  }
}
