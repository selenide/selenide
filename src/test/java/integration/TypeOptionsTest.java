package integration;

import com.codeborne.selenide.TypeOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static com.codeborne.selenide.Condition.attribute;

class TypeOptionsTest extends ITest {
  @BeforeEach
  void openAndInitTestPage() {
    openFile("page_with_inputevents_on_clear.html");
  }

  @Test
  void typeOptions() {
    $("#username_input").setValue("test");
    $("#username_input").type("abc");
    $("#username_input").shouldHave(attribute("value", "abc"));

    $("#username_input").type(TypeOptions.with("test", Duration.ofMillis(200), false));
    $("#username_input").shouldHave(attribute("value", "abctest"));

    $("#username_input").type(TypeOptions.withDelay("test", Duration.ofMillis(100)));
    $("#username_input").shouldHave(attribute("value", "test"));

    $("#username_input").type(TypeOptions.withoutClearingField("abcd"));
    $("#username_input").shouldHave(attribute("value", "testabcd"));
  }
}
