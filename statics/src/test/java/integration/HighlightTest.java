package integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.HighlightOptions.border;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

final class HighlightTest extends IntegrationTest {
  private static final long pause = 1L;

  @BeforeEach
  void openPage() {
    openFile("page_with_inputs_and_hints.html");
  }

  @Test
  void canHighlightElements() {
    $("#username").highlight(border("3px solid blue")).setValue("john");
    sleep(pause);
    $("#usernameHint").highlight();
    sleep(pause);
    $("#password").highlight(border("3px solid red")).setValue("admin");
    sleep(pause);
    $("#passwordHint").highlight(border("3px solid green")).should(appear);
    sleep(pause);
  }

}
