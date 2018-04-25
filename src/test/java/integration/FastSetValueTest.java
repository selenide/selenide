package integration;

import com.codeborne.selenide.Configuration;
import org.junit.Before;
import org.junit.Test;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Selenide.$;

public class FastSetValueTest extends IntegrationTest {
  @Before
  public void openTestPageWithJQuery() {
    openFile("page_with_inputs_and_hints.html");
  }

  @Test
  public void standardSetValueTriggersBlurCorrectly() {
    Configuration.fastSetValue = false;
    $("#username").setValue("john");
    $("#usernameHint").should(appear);

    $("#password").setValue("admin");
    $("#usernameHint").should(disappear);
    $("#passwordHint").should(appear);
  }

  @Test
  public void fastSetValueTriggersBlurCorrectly() {
    Configuration.fastSetValue = true;
    $("#username").setValue("john");
    $("#usernameHint").should(appear);

    $("#password").setValue("admin");
    $("#usernameHint").should(disappear);
    $("#passwordHint").should(appear);
  }

  @Test
  public void fastSetValue_withoutChangeEvent() {
    Configuration.fastSetValue = true;
    Configuration.setValueChangeEvent = false;
    $("#username").setValue("john");
    $("#usernameHint").should(appear);

    $("#password").setValue("admin");
    $("#usernameHint").should(disappear);
    $("#passwordHint").should(appear);
  }
}
