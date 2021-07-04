package integration;

import com.codeborne.selenide.*;
import org.junit.jupiter.api.*;

import static com.codeborne.selenide.Condition.*;
import static java.lang.Thread.*;

public class SetValueEventsTest extends ITest {
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
    $("#username_input").setValue(null);
    $("#usernameHint").should(disappear);
  }

  @Test
  void seleniumClearDoesNotTrigger() {
    $("#username_input").clear();
    $("#usernameHint").shouldNot(disappear); // Yes, doesn't trigger!
  }
}
